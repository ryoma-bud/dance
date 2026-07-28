package com.example.demo.service.profile;

import com.example.demo.common.normalizer.GenreNormalizer;
import com.example.demo.domain.entity.genre.GenreEntity;
import com.example.demo.domain.entity.UserEntity;
import com.example.demo.domain.repository.profile.GenreRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserGenreUpdater {

    private final GenreNormalizer genreNormalizer;
    private final GenreRepository genreRepository;

    public UserGenreUpdater(
            GenreNormalizer genreNormalizer,
            GenreRepository genreRepository
    ) {
        this.genreNormalizer = genreNormalizer;
        this.genreRepository = genreRepository;
    }

    public void replaceUserGenres(UserEntity user, List<String> rawGenres) {
        Set<String> genreCodes = genreNormalizer.normalizeGenreCodes(rawGenres);

        if (genreCodes.isEmpty()) {
            user.replaceGenres(Set.of());
            return;
        }

        List<GenreEntity> genres = genreRepository.findByCodeInAndActiveTrue(genreCodes);

        if (genres.size() != genreCodes.size()) {
            throw new IllegalArgumentException("存在しないジャンルが含まれています");
        }

        user.replaceGenres(new HashSet<>(genres));
    }
}