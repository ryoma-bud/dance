package com.example.demo.domain.repository;

import com.example.demo.domain.entity.UserFollowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollowEntity, Long> {

    // もうFollowしているかを確認
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long FollowingId);

    // UnFollow処理能のためFollow関係照会（一件だけ）
    Optional<UserFollowEntity> findByFollowerIdAndFollowingId(Long followerId, Long FollowingId);

    // 自分がFollowしているリスト照会
    List<UserFollowEntity> findByFollowerId(Long followerId);

    // 自分をFollowしているリスト照会
    List<UserFollowEntity> findByFollowingId(Long followingId);

    // Following 数をカウント
    long countByFollowerId(Long followerId);

    // Follower数をカウント
    long countByFollowingId(Long followingId);
}
