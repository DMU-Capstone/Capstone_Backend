package com.waitit.capstone.domain.admin;

import com.waitit.capstone.domain.admin.dto.AllHostRequest;
import com.waitit.capstone.domain.admin.dto.AllUserRequest;
import com.waitit.capstone.domain.admin.dto.HostSummaryDto;
import com.waitit.capstone.domain.admin.dto.UpdatedRequest;

import com.waitit.capstone.domain.image.ImageService;
import com.waitit.capstone.domain.image.repository.EventImageRepository;
import com.waitit.capstone.domain.manager.Host;
import com.waitit.capstone.domain.manager.HostRepository;
import com.waitit.capstone.domain.member.Entity.Member;
import com.waitit.capstone.domain.member.MemberRepository;
import com.waitit.capstone.domain.queue.service.QueueService;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import com.waitit.capstone.global.util.PageResponse;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
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
    private final EventImageRepository eventImageRepository;
    private final RedissonClient redissonClient;
    private static final String ACTIVE_HOSTS_KEY = "active:hosts";


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
        RList<QueueDto> queue = redissonClient.getList("waitList:" + hostId);
        return queue.readAll();
    }

    public void deleteImage(Long id) {
        eventImageRepository.deleteById(id);
    }
}
