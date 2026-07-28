package com.example.demo.domain.entity.genre;

import com.example.demo.domain.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_genres",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_genres_user_genre",
                        columnNames = {"user_id", "genre_id"}
                )
        },
        indexes = {
                @Index(name = "idx_user_genres_user_id", columnList = "user_id"),
                @Index(name = "idx_user_genres_genre_id", columnList = "genre_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class UserGenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // どのユーザーのジャンルか
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // どのジャンルか
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "genre_id", nullable = false)
    private GenreEntity genre;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public UserGenreEntity(UserEntity user, GenreEntity genre) {
        this.user = user;
        this.genre = genre;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}