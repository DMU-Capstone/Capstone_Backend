package com.waitit.capstone.domain.main.search;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Keyword {

    public Keyword(String search_term,String user_ip) {
        this.search_term = search_term;
        this.user_ip = user_ip;
    }

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime searchDate;
    private String search_term;
    private String user_ip;

    @PrePersist
    protected void onCreate() {
        this.searchDate = LocalDateTime.now();
    }


}
