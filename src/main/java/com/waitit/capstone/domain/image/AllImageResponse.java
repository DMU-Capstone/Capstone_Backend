package com.waitit.capstone.domain.image;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AllImageResponse {
    private Long id;
    private String dbFilePath;
    private LocalDateTime createdAt;
}
