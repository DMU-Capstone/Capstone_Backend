package com.waitit.capstone.domain.dashboard.dto;

import com.waitit.capstone.domain.dashboard.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class ReviewSummaryDto {
    private String date;
    private int rating;
    private String comment;

    public static ReviewSummaryDto from(Review review) {
        return ReviewSummaryDto.builder()
                .date(review.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .rating(review.getRating())
                .comment(review.getComment())
                .build();
    }
}
