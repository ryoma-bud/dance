package com.example.demo.domain.repository;


import com.example.demo.domain.entity.post.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query("SELECT DISTINCT p FROM PostEntity p " +
            "LEFT JOIN FETCH p.user " +
            "LEFT JOIN FETCH p.medias " +
            "WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "ORDER BY p.createdAt DESC")
    List<PostEntity> searchByContent(@Param("query") String query);

}
