package com.example.demo.domain.dto.profile;

import com.example.demo.domain.entity.Genre;
import com.example.demo.domain.entity.Role;

import java.time.LocalDate;

public record UserProfileResponse(
        Long id,
        String name,
        Role role,
        String email,
        String profileText,
        String profileImageUrl,
        Genre genre,
        LocalDate birthDate
) {
}
