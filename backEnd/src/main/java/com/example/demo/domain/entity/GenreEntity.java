package com.example.demo.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "genres",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_genres_code", columnNames = "code")
        },
        indexes = {
                @Index(name = "idx_genres_active_sort_order", columnList = "active, sort_order")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class GenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // APIやDB内部で使う固定コード
    @Column(nullable = false, length = 50)
    private String code;

    // 画面表示用の名前
    @Column(nullable = false, length = 100)
    private String displayName;

    // 表示順
    @Column(nullable = false)
    private Integer sortOrder;

    // 無効化したジャンルを残すためのフラグ
    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public GenreEntity(String code, String displayName, Integer sortOrder) {
        this.code = code;
        this.displayName = displayName;
        this.sortOrder = sortOrder;
        this.active = true;
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