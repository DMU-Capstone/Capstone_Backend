package com.waitit.capstone.domain.admin;

import com.waitit.capstone.domain.admin.dto.AllHostRequest;
import com.waitit.capstone.domain.admin.dto.AllUserRequest;
import com.waitit.capstone.domain.admin.dto.HostSummaryDto;
import com.waitit.capstone.domain.admin.dto.MainBannerResponse;
import com.waitit.capstone.domain.admin.dto.UpdatedRequest;

import com.waitit.capstone.domain.image.ImageService;
import com.waitit.capstone.domain.manager.Host;
import com.waitit.capstone.domain.manager.HostRepository;
import com.waitit.capstone.domain.member.Entity.Member;
import com.waitit.capstone.domain.member.MemberRepository;
import com.waitit.capstone.domain.queue.QueueService;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import com.waitit.capstone.global.util.PageResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@Service
public class AdminService {

    private final MemberRepository memberRepository;
    private final AdminMapper adminMapper;
    private final HostRepository hostRepository;
    private final ImageService imageService;
    private final QueueService queueService;
    private final StringRedisTemplate redisTemplate;


    //모든 유저를 조회후 페이징
    public PageResponse<AllUserRequest> getAllUser(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        Page<AllUserRequest> allUserRequests = members.map(adminMapper::toAllUserRequest);

        return new PageResponse<>(allUserRequests);
    }

    //유저 리퀘스트 바디를 받아서 멤버 수정후 저장
    public void updateMember(UpdatedRequest request) {
        Long memberId = Long.parseLong(request.getId());
        Member member = memberRepository.findMemberById(memberId);

        member.updateProfile(request.getName(), request.getName(), request.getPassword());
        memberRepository.save(member);
    }

    //아이디로 멤버삭제
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    //이벤트 배너 등록
    public void uploadEventImage(List<MultipartFile> images) {
        imageService.uploadEvent(images);
    }

    //이벤트 배너 메인 등록
    public void selectBanner(Long imgId, int number) {
        //레디스에 imgId 받은걸 db 패스를 찾음
        String img = imageService.getImgPath(imgId);
        String redisKey = "main_banner";
        // 레디스 리스트의  number 인덱스에 등록
        redisTemplate.opsForList().set(redisKey, number, img);
    }

    //모든 대기열 내역 조회
    public PageResponse<AllHostRequest> getAllHost(Pageable pageable) {
        Page<Host> hosts = hostRepository.findAll(pageable);
        Page<AllHostRequest> allHostRequests = hosts.map(adminMapper::toAllHostRequest);

        return new PageResponse<>(allHostRequests);
    }

    public MainBannerResponse getEventBanner() {
        String redisKey = "main_banner";
        List<String> list = redisTemplate.opsForList().range(redisKey, 0, 4);
        return new MainBannerResponse("mainBanner", list);
    }
    //현재 대기열 목록 조회
    public List<HostSummaryDto> getActiveHostSummaries() {
        Set<String> ids = redisTemplate.opsForSet().members("active:hosts");

        return ids.stream()
                .map(Long::parseLong)
                .map(hostRepository::findById)
                .filter(Optional::isPresent)
                .map(opt -> {
                    Host host = opt.get();
                    return new HostSummaryDto(
                            host.getId(),
                            host.getHostName(),
                            host.getImages().stream().findFirst().orElse(null)
                    );
                })
                .toList();
    }
    //각 대기열 세부 목록 조회
    public List<QueueDto> getQueueDtoByHostId(String hostId) {
        String key = "waitList" + hostId;
        List<String> rawList = redisTemplate.opsForList().range(key, 0, -1);

        if (rawList == null) return List.of();

        return rawList.stream()
                .filter(s -> s != null && s.trim().startsWith("{")) // JSON 객체만
                .map(s -> {
                    try {
                        return queueService.convertStringToDto(s);
                    } catch (RuntimeException e) {
                        System.err.println("[QueueDto 역직렬화 실패] 값: " + s);
                        return null; // 실패한 값은 무시
                    }
                })
                .filter(Objects::nonNull) // null 제거
                .toList();
    }
}
