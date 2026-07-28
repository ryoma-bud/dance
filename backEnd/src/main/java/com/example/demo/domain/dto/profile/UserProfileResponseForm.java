package com.example.demo.domain.dto.profile;

import com.example.demo.domain.entity.Role;

import java.time.LocalDate;
import java.util.List;

public record UserProfileResponseForm(
        Long id,
        String name,
        Role role,
        String email,
        String profileText,
        String profileImageUrl,
        List<String> genres,
        LocalDate birthDate
) {
}
