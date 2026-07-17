package com.example.demo.domain.dto.userDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpResponseDto {

    private Long id;
    private String name;
    private String email;
    private String message;

}
