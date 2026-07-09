package com.example.demo.domain.dto.userDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoDto {

    private Long id;
    private String name;
    private String email;
    private String role;
}
