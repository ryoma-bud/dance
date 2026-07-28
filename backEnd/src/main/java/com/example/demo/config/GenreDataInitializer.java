package com.example.demo.config;

import com.example.demo.domain.entity.genre.GenreEntity;
import com.example.demo.domain.repository.profile.GenreRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class GenreDataInitializer implements CommandLineRunner {

    private final GenreRepository genreRepository;

    public GenreDataInitializer(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<GenreSeed> genreSeeds = List.of(
                new GenreSeed("HIPHOP", "ヒップホップ", 10),
                new GenreSeed("JAZZ", "ジャズ", 20),
                new GenreSeed("BREAK", "ブレイク", 30),
                new GenreSeed("LOCK", "ロック", 40),
                new GenreSeed("POP", "ポップ", 50),
                new GenreSeed("HOUSE", "ハウス", 60),
                new GenreSeed("KPOP", "KPOP", 70),
                new GenreSeed("FREESTYLE", "フリースタイル", 80),
                new GenreSeed("OTHER", "その他", 90)
        );

        for (GenreSeed seed : genreSeeds) {
            if (genreRepository.existsByCode(seed.code())) {
                continue;
            }

            GenreEntity genre = new GenreEntity(
                    seed.code(),
                    seed.displayName(),
                    seed.sortOrder()
            );

            genreRepository.save(genre);
        }
    }

    private record GenreSeed(
            String code,
            String displayName,
            Integer sortOrder
    ) {
    }
}