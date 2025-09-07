package com.waitit.capstone.domain.main.search;

import com.waitit.capstone.domain.manager.dto.SessionListDto;
import com.waitit.capstone.domain.main.search.dto.SearchTermCountDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "검색 API", description = "가게 검색 관련 API")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "키워드 검색", description = "사용자가 입력한 키워드로 가게를 검색합니다.")
    @GetMapping
    public ResponseEntity<?> findKeyword(@RequestParam String term, HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        List<SessionListDto> list =  searchService.findKeyword(term,ip);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "인기 검색어 조회", description = "현재 가장 인기있는 검색어 목록을 조회합니다.")
    @GetMapping("/top")
    public ResponseEntity<?> findTopKeyword(){
        List<SearchTermCountDto>list = searchService.findTopKeyword();
        return ResponseEntity.ok(list);
    }
}
