package com.example.demo.domain.repository.profile;

import com.example.demo.domain.entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GenreRepository extends JpaRepository<GenreEntity, Long> {

    Optional<GenreEntity> findByCode(String code);

    boolean existsByCode(String code);

    List<GenreEntity> findByCodeInAndActiveTrue(Collection<String> codes);

    List<GenreEntity> findByActiveTrueOrderBySortOrderAsc();
}