package com.example.demo.domain.dto.searchDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchResponseDto {

    private Long id;
    private String content;
    private String thumbnailUrl;
    private Long authorId;
    private String authorName;
    private String authorProfileImageUrl;
    private LocalDateTime createdAt;
}
