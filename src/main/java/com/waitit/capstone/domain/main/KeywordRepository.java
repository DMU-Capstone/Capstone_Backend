package com.waitit.capstone.domain.main;

import com.waitit.capstone.domain.main.dto.SearchTermCountDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword,Long> {
    @Query(value = "select search_term AS searchTerm, COUNT(*) AS count from Keyword group by search_term order by count desc limit 10", nativeQuery = true)
    List<SearchTermCountDto> findTopSearchTerm();
}
