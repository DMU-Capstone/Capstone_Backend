package com.waitit.capstone.domain.main;

import com.waitit.capstone.domain.image.entity.HostImage;
import com.waitit.capstone.domain.main.dto.SearchTermCountDto;
import com.waitit.capstone.domain.manager.Host;
import com.waitit.capstone.domain.manager.HostRepository;
import com.waitit.capstone.domain.manager.HostService;
import com.waitit.capstone.domain.manager.dto.SessionListDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MainService {


    private final HostService hostService;
    private final KeywordRepository keywordRepository;
    private final StringRedisTemplate redisTemplate;
    private final HostRepository hostRepository;
    //트렌드 호스트
    public List<?> findTrendHost(int count){
        Set<String> latestHostIds = redisTemplate.opsForZSet()
                .reverseRange("sorted:hosts", 0, count - 1); // 최신 등록 순

        if (latestHostIds == null || latestHostIds.isEmpty()) return List.of();

        List<Long> ids = latestHostIds.stream()
                .map(Long::valueOf)
                .toList();

        List<Host> hosts = hostRepository.findAllById(ids);

        // 정렬 보존: ZSet의 순서 → DB 결과의 순서 보장 X → 다시 정렬 필요
        List<Host> sorted = ids.stream()
                .map(id -> hosts.stream().filter(h -> h.getId().equals(id)).findFirst().orElse(null))
                .filter(h -> h != null)
                .toList();

        return sorted.stream().map(host -> {
            String imgUrl = host.getImages().stream()
                    .findFirst()
                    .map(HostImage::getImgPath)
                    .orElse(null);

            int waiting = Optional.ofNullable(redisTemplate.opsForList()
                            .size("waitList" + host.getId()))
                    .map(Long::intValue)
                    .orElse(0);

            return SessionListDto.builder()
                    .hostId(host.getId())
                    .hostName(host.getHostName())
                    .imgUrl(imgUrl)
                    .waitingCount(waiting)
                    .estimatedTime(calculateEstimatedTime(host.getStartTime(), host.getEndTime()))
                    .build();
        }).toList();


    }
    //검색기능
    public List<SessionListDto> findKeyword(String keyword,String user_ip){
        Keyword key = new Keyword(keyword,user_ip);
        keywordRepository.save(key);
        List<SessionListDto> list = hostService.getAllSessions();
        List<SessionListDto> results = new ArrayList<>();
        for(SessionListDto i : list){
            if(i.hostName().contains(keyword)){
                results.add(i);
            }
        }
        return results;
    }


    //핫한 키워드
    public List<SearchTermCountDto> findTopKeyword(){
        return keywordRepository.findTopSearchTerm();
    }
    private String calculateEstimatedTime(LocalDateTime startTime, LocalDateTime endTime) {
        return null;
    }
}
