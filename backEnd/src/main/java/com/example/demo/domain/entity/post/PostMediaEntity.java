package com.example.demo.domain.entity.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;

@Entity
@Table(name = "post_medias")
@Getter
@Setter
@NoArgsConstructor
public class PostMediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Column(nullable = false)
    private String mediaUrl; // File保存する場所

    private String thumbnailUrl; // 動画の場合Thumbnailのイメージを保存する場所

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    private Integer sortOrder = 0;  // Slideの順番

}
