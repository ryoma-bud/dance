package com.example.demo.domain.dto.userDto;

import com.example.demo.domain.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowResponse {
    private Long id;
    private String name;
    private String email;
    private String profileImageUrl;

    // UserEntity -> FollowResponseへ変換
    public static FollowResponse from(UserEntity userEntity) {
        return FollowResponse.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .profileImageUrl(userEntity.getProfileImageUrl())
                .build();
    }
}
