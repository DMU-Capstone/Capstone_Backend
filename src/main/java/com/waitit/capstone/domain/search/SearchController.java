package com.waitit.capstone.domain.search;

import com.waitit.capstone.domain.manager.dto.SessionListDto;
import com.waitit.capstone.domain.search.dto.SearchTermCountDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<?> findKeyword(@RequestParam String term, HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        List<SessionListDto> list =  searchService.findKeyword(term,ip);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/top")
    public ResponseEntity<?> findTopKeyword(){
        List<SearchTermCountDto>list = searchService.findTopKeyword();
        return ResponseEntity.ok(list);
    }
}
