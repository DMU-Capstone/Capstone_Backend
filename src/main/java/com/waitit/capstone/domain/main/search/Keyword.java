package com.waitit.capstone.domain.main.search;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Keyword")
@AllArgsConstructor
@NoArgsConstructor
public class Keyword {

    public Keyword(String searchTerm, String userIp) {
        this.searchTerm = searchTerm;
        this.userIp = userIp;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "searched_at")
    private LocalDateTime searchedAt;

    @Column(name = "search_term")
    private String searchTerm;

    @Column(name = "user_ip")
    private String userIp;

    @PrePersist
    protected void onCreate() {
        this.searchedAt = LocalDateTime.now();
    }
}
