package com.example.demo.domain.dto.searchDto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResponseDto {

    private Long id;
    private String name;
    private String email;
    private String profileImageUrl;
    private long followerCount;
    private boolean isFollowing;
}
