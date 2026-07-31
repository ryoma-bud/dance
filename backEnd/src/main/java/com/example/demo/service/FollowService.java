package com.example.demo.service;

import com.example.demo.domain.entity.UserEntity;
import com.example.demo.domain.entity.UserFollowEntity;
import com.example.demo.domain.repository.UserFollowRepository;
import com.example.demo.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;

    // ##########################################
    // Follow/UnFollow Toggle
    @Transactional
    public boolean toggleFollow(Long followerId, Long followingId) {

        //　自分はFollowできない
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("自分をフォローすることはできません。");
        }

        Optional<UserFollowEntity> existingFollow = userFollowRepository.findByFollowerIdAndFollowingId(followerId, followingId);

        if (existingFollow.isPresent()) {
            userFollowRepository.delete(existingFollow.get());
            return false;
        } else {
            // Followしてなかったら、追加（Follow）
            UserEntity follower = userRepository.findById(followerId)
                    .orElseThrow(() -> new IllegalArgumentException("Follow申請者が見つかりませんでした。" + followerId));
            UserEntity following = userRepository.findById(followingId)
                    .orElseThrow(() -> new IllegalArgumentException("팔로우 대상을 찾을 수 없습니다. ID: " + followingId));

            UserFollowEntity userFollowEntity = new UserFollowEntity();
            userFollowEntity.setFollower(follower);
            userFollowEntity.setFollowing(following);
            userFollowRepository.save(userFollowEntity);

            return true;
        }
    }

    // #####################################
    // Followしているかを確認
    // #####################################
    public boolean isFollowing(Long followerId, Long followingId) {
        return userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    // #####################################
    // 自分がFollowしているかを確認
    // #####################################
    public List<UserEntity> getFollowingList(Long userId) {
        return userFollowRepository.findByFollowerId(userId)
                .stream()
                .map(UserFollowEntity::getFollowing)
                .toList();
    }

    // #####################################
    // 自分をFollowしているUserList照会
    // #####################################
    public List<UserEntity> getFollowerList(Long userId) {
        return userFollowRepository.findByFollowingId(userId)
                .stream()
                .map(UserFollowEntity::getFollower)
                .toList();
    }

    // #####################################
    // Followingの数確認
    // #####################################
    public long getFollowingCount(Long userId) {
        return userFollowRepository.countByFollowingId(userId);
    }

    // #####################################
    // Followerの数確認
    // #####################################
    public long getFollowerCount(Long userId) {
        return userFollowRepository.countByFollowerId(userId);
    }


}