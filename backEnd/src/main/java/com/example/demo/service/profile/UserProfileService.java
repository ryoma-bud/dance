package com.example.demo.service.profile;

import com.example.demo.domain.dto.profile.UserProfileResponse;
import com.example.demo.domain.dto.profile.UserProfileUpdate;
import com.example.demo.domain.entity.UserEntity;
import com.example.demo.domain.repository.profile.UserProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static com.example.demo.service.Common.normalizeNullableText;

@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    private static final String PROFILE_ICON_URL_PREFIX = "/uploads/profile-icons";
    private static final String PROFILE_ICON_DIR = "C:/dance_uploads/profile-icons";

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        UserEntity user = findUser(userId);
        return toResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(
            Long userId,
            UserProfileUpdate request,
            MultipartFile iconFile
    ) {
        UserEntity user = findUser(userId);

        String name = request.name() == null ? "" : request.name().trim();
        String email = request.email() == null ? "" : request.email().trim().toLowerCase();

        if (!StringUtils.hasText(name)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ユーザーネームを入力してください"
            );
        }

        if (!StringUtils.hasText(email)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "メールアドレスを入力してください"
            );
        }

        user.setName(name);
        user.setEmail(email);
        user.setRole(request.role());
        user.setProfileText(normalizeNullableText(request.profileText()));
        user.setGenre(request.genre());
        user.setBirthDate(request.birthDate());

        if (iconFile != null && !iconFile.isEmpty()) {
            String profileImageUrl = saveProfileIcon(userId, iconFile);
            user.setProfileImageUrl(profileImageUrl);
        } else if (request.profileImageUrl() != null) {
            user.setProfileImageUrl(normalizeNullableText(request.profileImageUrl()));
        }

        return toResponse(user);
    }

    private String saveProfileIcon(Long userId, MultipartFile iconFile) {
        String contentType = iconFile.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "画像ファイルを選択してください"
            );
        }

        String originalFilename = iconFile.getOriginalFilename();
        String extension = getExtension(originalFilename);

        String filename = "user-" + userId + "-" + UUID.randomUUID() + extension;

        try {
            Path uploadDir = Paths.get(PROFILE_ICON_DIR);
            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(filename);
            iconFile.transferTo(filePath.toFile());

            return PROFILE_ICON_URL_PREFIX + "/" + filename;
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "アイコン画像の保存に失敗しました"
            );
        }
    }

    private String getExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return ".png";
        }

        return filename.substring(filename.lastIndexOf("."));
    }

    private UserEntity findUser(Long userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "ユーザーが見つかりません"
                ));
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
}