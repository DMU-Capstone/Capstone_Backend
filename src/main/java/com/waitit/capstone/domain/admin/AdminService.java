package com.waitit.capstone.domain.admin;

import com.waitit.capstone.domain.admin.dto.*; // DTO 임포트
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
    private static final String MAIN_BANNERS_KEY = "main_banners"; // Redis 키 이름 변경

    // ... (getAllUser, updateMember, deleteMember 생략)
    public PageResponse<AllUserRequest> getAllUser(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        Page<AllUserRequest> allUserRequests = members.map(adminMapper::toAllUserRequest);
        return new PageResponse<>(allUserRequests);
    }

    public void updateMember(UpdatedRequest request) {
        Long memberId = Long.parseLong(request.getId());
        Member member = memberRepository.findMemberById(memberId);
        member.updateProfile(request.getName(), request.getName(), request.getPassword());
        memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    //이벤트 배너 등록
    public void uploadEventImage(List<MultipartFile> images) {
        imageService.uploadEvent(images);
    }

    /**
     * 메인 배너의 상태(ON/OFF)를 변경합니다.
     * @param request 배너 ID와 활성화 상태(active)를 담은 요청
     */
    public void updateBannerStatus(UpdateBannerStatusRequest request) {
        // 1. imgId로 DB에서 이미지 경로를 조회합니다.
        String imgPath = imageService.getImgPath(request.getImgId());

        // 2. 요청의 active 상태에 따라 Redis SET에 추가 또는 삭제합니다.
        if (request.isActive()) {
            // ON: SET에 이미지 경로를 추가합니다.
            redisTemplate.opsForSet().add(MAIN_BANNERS_KEY, imgPath);
        } else {
            // OFF: SET에서 이미지 경로를 제거합니다.
            redisTemplate.opsForSet().remove(MAIN_BANNERS_KEY, imgPath);
        }
    }

    // ... (기존 getAllHost, getActiveHostSummaries 등 생략)
    public PageResponse<AllHostRequest> getAllHost(Pageable pageable) {
        Page<Host> hosts = hostRepository.findAll(pageable);
        Page<AllHostRequest> allHostRequests = hosts.map(adminMapper::toAllHostRequest);
        return new PageResponse<>(allHostRequests);
    }

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

    public List<QueueDto> getQueueDtoByHostId(String hostId) {
        RList<QueueDto> queue = redissonClient.getList("waitList:" + hostId);
        return queue.readAll();
    }

    public void deleteImage(Long id) {
        eventImageRepository.deleteById(id);
    }
}
