package com.example.demo.domain.dto.userDto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {

    private String token;
    private UserInfoDto userInfoDto;
}
