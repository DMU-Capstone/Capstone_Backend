package com.waitit.capstone.domain.main;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Persistent;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Keyword {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime searchDate;
    private String search_term;

    @PrePersist
    protected void onCreate() {
        this.searchDate = LocalDateTime.now();
    }
}
