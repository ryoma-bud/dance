package com.example.demo.service;

import com.example.demo.domain.dto.searchDto.IntegratedSearchResponseDto;
import com.example.demo.domain.dto.searchDto.PostSearchResponseDto;
import com.example.demo.domain.dto.searchDto.UserSearchResponseDto;
import com.example.demo.domain.entity.UserEntity;
import com.example.demo.domain.entity.post.MediaType;
import com.example.demo.domain.entity.post.PostEntity;
import com.example.demo.domain.entity.post.PostMediaEntity;
import com.example.demo.domain.repository.PostRepository;
import com.example.demo.domain.repository.UserFollowRepository;
import com.example.demo.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserFollowRepository userFollowRepository;

    public IntegratedSearchResponseDto searchAll(String query, Long currentUserId) {

        // Search User
        List<UserEntity> userEntities = userRepository.searchByNameOrEmail(query);
        List<UserSearchResponseDto> users = userEntities.stream()
                .filter(user -> !user.getId().equals(currentUserId))    // 本人は検索されないように
                .map(user -> {
                    long followerCount = userFollowRepository.countByFollowingId(user.getId());
                    boolean isFollowing = currentUserId != null &&
                            userFollowRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId());

                    return UserSearchResponseDto.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .profileImageUrl(user.getProfileImageUrl())
                            .followerCount(followerCount)
                            .isFollowing(isFollowing)
                            .build();
                })
                .toList();

        // Search Post
        List<PostEntity> postEntities = postRepository.searchByContent(query);
        List<PostSearchResponseDto> posts = postEntities.stream()
                .map(post -> {
                    // 첫 번째 미디어(sortOrder가 가장 낮은 미디어)의 썸네일/이미지 URL 추출
                    String thumbnailUrl = post.getMedias().stream()
                            .min(Comparator.comparingInt(PostMediaEntity::getSortOrder))
                            .map(media -> media.getMediaType() == MediaType.VIDEO && media.getThumbnailUrl() != null
                                    ? media.getThumbnailUrl()
                                    : media.getMediaUrl())
                            .orElse(null);

                    return PostSearchResponseDto.builder()
                            .id(post.getId())
                            .content(post.getContent())
                            .thumbnailUrl(thumbnailUrl)
                            .authorId(post.getUser().getId())
                            .authorName(post.getUser().getName())
                            .authorProfileImageUrl(post.getUser().getProfileImageUrl())
                            .createdAt(post.getCreatedAt())
                            .build();
                })
                .toList();

        return IntegratedSearchResponseDto.builder()
                .users(users)
                .posts(posts)
                .build();
    }
}
