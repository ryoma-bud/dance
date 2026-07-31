package com.example.demo.controller;

import com.example.demo.domain.dto.userDto.FollowResponse;
import com.example.demo.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // ###########################################
    // Follow / UnFollow Toggle Api
    // ###########################################
    @PostMapping("/{targetUserId}")
    public ResponseEntity<Boolean> toggleFollow(
            @RequestAttribute Long currentUserId,
            @PathVariable Long targetUserId) {

        boolean isFollowing = followService.toggleFollow(currentUserId, targetUserId);

        return ResponseEntity.ok(isFollowing);
    }

    // ###########################################
    // Follow 確認API
    // ###########################################
    @GetMapping("/{targetUserId}/status")
    public ResponseEntity<Boolean> isFollowing (
            @RequestAttribute Long currentUserId,
            @PathVariable Long targetUserId) {

        boolean isFollowing = followService.isFollowing(currentUserId, targetUserId);
        return ResponseEntity.ok(isFollowing);
    }

    // #####################################
    // 特定のユーザーのFollowingリスト照会API
    // #####################################
    @GetMapping("/{userId}/followings")
    public ResponseEntity<List<FollowResponse>> getFollowingList(@PathVariable Long userId) {
        List<FollowResponse> followings = followService.getFollowingList(userId)
                .stream()
                .map(FollowResponse::from)
                .toList();

        return ResponseEntity.ok(followings);
    }

    // #####################################
    // 特定のユーザーのFollower/Followingの数照会API
    // #####################################
    public ResponseEntity<FollowCountResponse> getFollowCount(@PathVariable Long userId) {

        long followerCount = followService.getFollowerCount(userId);
        long followingCount = followService.getFollowingCount(userId);

        return ResponseEntity.ok(new FollowCountResponse(followerCount, followingCount));
    }

    // Count用内部のrecord DTO
    public record FollowCountResponse(long followerCount, long followingCount) {}
}
