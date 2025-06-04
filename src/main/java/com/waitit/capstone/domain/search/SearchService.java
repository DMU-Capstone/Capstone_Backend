package com.waitit.capstone.domain.search;

import com.waitit.capstone.domain.search.dto.SearchTermCountDto;
import com.waitit.capstone.domain.manager.HostService;
import com.waitit.capstone.domain.manager.dto.SessionListDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SearchService {

    private final HostService hostService;
    private final KeywordRepository keywordRepository;

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

}
