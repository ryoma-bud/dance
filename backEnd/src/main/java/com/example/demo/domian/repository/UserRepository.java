package com.example.demo.domian.repository;

import com.example.demo.domian.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);    // 会員登録時、emailが被っているかを確認

    boolean existsByName(String name);  // 名前がかぶっているか確認

    Optional<UserEntity> findByEmail(String email); // LoginしてるUserの会員情報取得用
}
