package com.example.demo.domain.repository;

import com.example.demo.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);    // 会員登録時、emailが被っているかを確認

    boolean existsByName(String name);  // 名前がかぶっているか確認

    Optional<UserEntity> findByEmail(String email); // LoginしてるUserの会員情報取得用

    // 名前やEmailが検索した言葉に含まれているUserを照会
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<UserEntity> searchByNameOrEmail(@Param("query") String query);
}
