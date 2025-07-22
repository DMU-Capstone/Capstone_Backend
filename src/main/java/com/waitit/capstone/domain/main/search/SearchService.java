package com.waitit.capstone.domain.main.search;

import com.waitit.capstone.domain.main.search.dto.SearchTermCountDto;
import com.waitit.capstone.domain.manager.service.HostService;
import com.waitit.capstone.domain.manager.dto.SessionListDto;
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

        return hostService.getAllSessions()
                .stream()
                .filter(i -> i.hostName().contains(keyword))
                .toList();
    }


    //핫한 키워드
    public List<SearchTermCountDto> findTopKeyword(){
        return keywordRepository.findTopSearchTerm();
    }

}
