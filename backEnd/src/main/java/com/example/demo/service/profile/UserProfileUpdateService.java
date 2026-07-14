package com.example.demo.service.profile;

import com.example.demo.domain.dto.profile.UserProfileResponse;
import com.example.demo.domain.dto.profile.UserProfileUpdate;
import com.example.demo.domain.entity.Role;
import com.example.demo.domain.entity.UserEntity;
import com.example.demo.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.demo.exception.profile.BadRequestException;
import com.example.demo.exception.profile.ResourceNotFoundException;

@Service
public class UserProfileUpdateService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileUpdateService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        UserEntity user = findUser(userId);
        return toResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UserProfileUpdate request) {
        UserEntity user = findUser(userId);

        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailAndIdNot(normalizedEmail, userId)) {
            throw new BadRequestException("このメールアドレスはすでに使用されています");
        }

        if (request.role() == Role.ADMIN && user.getRole() != Role.ADMIN) {
            throw new BadRequestException("プロフィール編集画面から管理者権限には変更できません");
        }

        user.setName(request.name().trim());
        user.setEmail(normalizedEmail);
        user.setRole(request.role());
        user.setProfileText(normalizeNullableText(request.profileText()));
        user.setProfileImageUrl(normalizeNullableText(request.profileImageUrl()));
        user.setGenre(request.genre());
        user.setBirthDate(request.birthDate());

        if (StringUtils.hasText(request.newPassword())) {
            validatePassword(request.newPassword());
            user.setPassword(passwordEncoder.encode(request.newPassword()));
        }

        UserEntity savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    private UserEntity findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));
    }

    private UserProfileResponse toResponse(UserEntity user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getRole(),
                user.getEmail(),
                user.getProfileText(),
                user.getProfileImageUrl(),
                user.getGenre(),
                user.getBirthDate()
        );
    }

    private String normalizeNullableText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new BadRequestException("パスワードは8文字以上で入力してください");
        }

        if (password.length() > 72) {
            throw new BadRequestException("パスワードは72文字以内で入力してください");
        }
    }
}
