package com.example.demo.domain.entity;

import com.example.demo.domain.entity.genre.GenreEntity;
import com.example.demo.domain.entity.genre.UserGenreEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ユーザー名
    @Column(nullable = false)
    private String name;

    // USER / ORGANIZER を文字列としてDBに保存
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false, unique = true)
    private String email;

    // 必ずBCryptなどでハッシュ化した値を保存する
    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String profileText;

    private String profileImageUrl;

    // 複数ジャンルは user_genres テーブルで管理する
    @Setter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<UserGenreEntity> userGenres = new HashSet<>();

    private LocalDate birthDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void replaceGenres(Set<GenreEntity> newGenres) {
        if (newGenres == null) {
            newGenres = Set.of();
        }

        Set<Long> newGenreIds = newGenres.stream()
                .map(GenreEntity::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        userGenres.removeIf(userGenre -> {
            GenreEntity genre = userGenre.getGenre();

            if (genre == null || genre.getId() == null) {
                return true;
            }

            return !newGenreIds.contains(genre.getId());
        });

        Set<Long> currentGenreIds = userGenres.stream()
                .map(UserGenreEntity::getGenre)
                .filter(Objects::nonNull)
                .map(GenreEntity::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (GenreEntity genre : newGenres) {
            if (genre == null || genre.getId() == null) {
                continue;
            }

            if (currentGenreIds.contains(genre.getId())) {
                continue;
            }

            userGenres.add(new UserGenreEntity(this, genre));
        }
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}