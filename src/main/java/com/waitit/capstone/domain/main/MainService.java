package com.waitit.capstone.domain.main;

import com.waitit.capstone.domain.manager.HostService;
import com.waitit.capstone.domain.manager.dto.SessionListDto;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MainService {
    private static final String ACTIVE_HOSTS_KEY = "active:hosts";
    private final HostService hostService;

    //검색기능
    public List<SessionListDto> findKeyword(String keyword){
        List<SessionListDto> list = hostService.getAllSessions();
        List<SessionListDto> results = new ArrayList<>();
        for(SessionListDto i : list){
            if(i.hostName().equals(keyword)){
                results.add(i);
            }
        }
        return results;
    }

    //트렌드 코스

    //핫한 키워드
}
