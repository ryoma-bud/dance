package com.example.demo.domain.repository.profile;

import com.example.demo.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserEntity, Long> {
}