package com.example.demo.domain.dto.searchDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegratedSearchResponseDto {

    private List<UserSearchResponseDto> users;
    private List<PostSearchResponseDto> posts;
}
