package com.example.demo.service.profile;

import com.example.demo.domain.dto.profile.UserProfileResponseForm;
import com.example.demo.domain.dto.profile.UserProfileUpdateForm;
import com.example.demo.domain.entity.Role;
import com.example.demo.domain.entity.UserEntity;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.exception.profile.BadRequestException;
import com.example.demo.exception.profile.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;

@Service
public class UserProfileUpdateService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserGenreUpdater userGenreUpdater;

    public UserProfileUpdateService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserGenreUpdater userGenreUpdater
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userGenreUpdater = userGenreUpdater;
    }

    @Transactional(readOnly = true)
    public UserProfileResponseForm getProfile(Long userId) {
        UserEntity user = findUser(userId);
        return toResponse(user);
    }

    @Transactional
    public UserProfileResponseForm updateProfile(Long userId, UserProfileUpdateForm request) {
        UserEntity user = findUser(userId);

        String name = request.name() == null ? "" : request.name().trim();
        String normalizedEmail = request.email() == null
                ? ""
                : request.email().trim().toLowerCase();

        if (!StringUtils.hasText(name)) {
            throw new BadRequestException("ユーザーネームを入力してください");
        }

        if (!StringUtils.hasText(normalizedEmail)) {
            throw new BadRequestException("メールアドレスを入力してください");
        }

        if (userRepository.existsByEmailAndIdNot(normalizedEmail, userId)) {
            throw new BadRequestException("このメールアドレスはすでに使用されています");
        }

        if (request.role() == Role.ADMIN && user.getRole() != Role.ADMIN) {
            throw new BadRequestException("プロフィール編集画面から管理者権限には変更できません");
        }

        user.setName(name);
        user.setEmail(normalizedEmail);
        user.setRole(request.role());
        user.setProfileText(normalizeNullableText(request.profileText()));
        user.setProfileImageUrl(normalizeNullableText(request.profileImageUrl()));
        user.setBirthDate(request.birthDate());

        userGenreUpdater.replaceUserGenres(user, request.genres());

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

    private UserProfileResponseForm toResponse(UserEntity user) {
        return new UserProfileResponseForm(
                user.getId(),
                user.getName(),
                user.getRole(),
                user.getEmail(),
                user.getProfileText(),
                user.getProfileImageUrl(),
                getGenreCodes(user),
                user.getBirthDate()
        );
    }

    private List<String> getGenreCodes(UserEntity user) {
        if (user.getUserGenres() == null) {
            return List.of();
        }

        return user.getUserGenres().stream()
                .filter(userGenre -> userGenre.getGenre() != null)
                .sorted(Comparator.comparing(
                        userGenre -> userGenre.getGenre().getSortOrder(),
                        Comparator.nullsLast(Integer::compareTo)
                ))
                .map(userGenre -> userGenre.getGenre().getCode())
                .toList();
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