package com.waitit.capstone.domain.manager.service;

import com.waitit.capstone.domain.image.ImageService;
import com.waitit.capstone.domain.image.entity.HostImage;
import com.waitit.capstone.domain.manager.Host;
import com.waitit.capstone.domain.manager.HostMapper;
import com.waitit.capstone.domain.manager.HostRepository;
import com.waitit.capstone.domain.manager.dto.HostRequest;
import com.waitit.capstone.domain.manager.dto.HostResponse;
import com.waitit.capstone.domain.manager.dto.SessionListDto;
import com.waitit.capstone.domain.manager.dto.WaitingListDto;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import com.waitit.capstone.domain.queue.service.QueueService; // QueueService 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor // @AllArgsConstructor -> @RequiredArgsConstructor 변경
@Service
public class HostService {
    private final HostRepository hostRepository;
    private final HostMapper hostMapper;
    private final ImageService imageService;
    private final QueueService queueService; // Redisson 대신 QueueService 주입

    @Transactional
    public void saveHost(HostRequest request, List<MultipartFile> hostImages) throws IOException {
        Host host = hostMapper.toEntity(request);
        Host saved = hostRepository.save(host);

        if (hostImages != null) {
            for (MultipartFile file : hostImages) {
                HostImage img = imageService.uploadHost(saved.getId(), file);
                saved.addImage(img);
            }
            hostRepository.save(saved);
        }
        // Redis 관련 로직 모두 삭제
    }

    @Transactional
    public void deactivateHost(Long hostId) {
        // 인메모리 대기열에서 해당 가게의 큐를 제거
        queueService.deactivateQueue(hostId);
    }

    public HostResponse getHost(Long id) {
        Host host = hostRepository.findHostById(id)
                .orElseThrow(() -> new IllegalArgumentException("Host not found with id: " + id));
        return hostMapper.hostToDto(host);
    }

    public List<SessionListDto> getAllSessions() {
        // 활성화된 가게 ID 목록을 QueueService에서 가져옴
        Set<Long> activeIds = queueService.getActiveHostIds();

        if (activeIds.isEmpty()) {
            return List.of();
        }

        List<Host> hosts = hostRepository.findAllById(activeIds);

        return hosts.stream().map(host -> {
            String imgUrl = host.getImages().stream()
                    .filter(HostImage::isRepresentative)
                    .findFirst()
                    .map(HostImage::getImagePath)
                    .orElse(host.getImages().isEmpty() ? null : host.getImages().get(0).getImagePath());

            // 대기열 크기를 QueueService에서 조회
            int waiting = queueService.getQueueByHostId(host.getId()).size();

            return SessionListDto.builder()
                    .hostId(host.getId())
                    .hostName(host.getHostName())
                    .imgUrl(imgUrl)
                    .waitingCount(waiting)
                    .estimatedTime(calculateEstimatedTime(host.getStartTime(), host.getEndTime()))
                    .build();
        }).collect(Collectors.toList());
    }

    private String calculateEstimatedTime(LocalDateTime startTime, LocalDateTime endTime) {
        // 이 부분은 비즈니스 로직에 따라 구현 필요
        return null;
    }

    public List<WaitingListDto> getQueueListByHostId(Long hostId) {
        // 대기열 목록을 QueueService에서 조회
        List<QueueDto> dtoList = queueService.getQueueByHostId(hostId);
        return hostMapper.queueToWaiting(dtoList);
    }

    public List<SessionListDto> findTrendHost(int count) {
        // Redis 대신 DB에서 최신순으로 가게를 조회하는 로직으로 변경
        List<Host> latestHosts = hostRepository.findTopByOrderByIdDesc(PageRequest.of(0, count));

        return latestHosts.stream().map(host -> {
            String imgUrl = host.getImages().stream()
                    .filter(HostImage::isRepresentative)
                    .findFirst()
                    .map(HostImage::getImagePath)
                    .orElse(host.getImages().isEmpty() ? null : host.getImages().get(0).getImagePath());

            int waiting = queueService.getQueueByHostId(host.getId()).size();

            return SessionListDto.builder()
                    .hostId(host.getId())
                    .hostName(host.getHostName())
                    .imgUrl(imgUrl)
                    .waitingCount(waiting)
                    .estimatedTime(calculateEstimatedTime(host.getStartTime(), host.getEndTime()))
                    .build();
        }).collect(Collectors.toList());
    }
}
