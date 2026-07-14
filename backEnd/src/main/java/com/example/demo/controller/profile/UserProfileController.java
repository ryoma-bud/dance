package com.example.demo.controller.profile;

import com.example.demo.domain.dto.profile.UserProfileUpdate;
import com.example.demo.service.profile.UserProfileService;
import com.example.demo.domain.dto.profile.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/users")
@RestController
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/{userId}/profile")
    public UserProfileResponse getProfile(@PathVariable Long userId) {
        return userProfileService.getProfile(userId);
    }

    @PutMapping(
            value = "/{userId}/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UserProfileResponse updateProfile(
            @PathVariable Long userId,
            @Valid @RequestPart("profile") UserProfileUpdate request,
            @RequestPart(value = "iconFile", required = false) MultipartFile iconFile
    ) {
        return userProfileService.updateProfile(userId, request, iconFile);
    }
}