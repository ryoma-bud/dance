package com.example.demo.domain.dto.profile;

import com.example.demo.domain.entity.Genre;
import com.example.demo.domain.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record UserProfileUpdateForm(
        @NotBlank(message = "ユーザー名は必須です")
        @Size(max = 50, message = "ユーザー名は50文字以内で入力してください")
        String name,

        @NotNull(message = "ロールは必須です")
        Role role,

        @NotBlank(message = "メールアドレスは必須です")
        @Email(message = "メールアドレスの形式が正しくありません")
        @Size(max = 255, message = "メールアドレスは255文字以内で入力してください")
        String email,

        String newPassword,

        @Size(max = 1000, message = "自己紹介文は1000文字以内で入力してください")
        String profileText,

        String profileImageUrl,

        List<String> genres,

        LocalDate birthDate
) {
}