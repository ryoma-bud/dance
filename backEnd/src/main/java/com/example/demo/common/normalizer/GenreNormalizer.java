package com.example.demo.common.normalizer;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class GenreNormalizer {

    private static final int MAX_GENRE_COUNT = 10;

    private static final Map<String, String> ALIASES = Map.ofEntries(
            Map.entry("ヒップホップ", "HIPHOP"),
            Map.entry("HIP HOP", "HIPHOP"),
            Map.entry("HIPHOP", "HIPHOP"),

            Map.entry("ジャズ", "JAZZ"),
            Map.entry("JAZZ", "JAZZ"),

            Map.entry("ブレイク", "BREAK"),
            Map.entry("BREAK", "BREAK"),

            Map.entry("ロック", "LOCK"),
            Map.entry("LOCK", "LOCK"),

            Map.entry("ポップ", "POP"),
            Map.entry("POP", "POP"),

            Map.entry("ハウス", "HOUSE"),
            Map.entry("HOUSE", "HOUSE"),

            Map.entry("KPOP", "KPOP"),
            Map.entry("K-POP", "KPOP"),

            Map.entry("フリースタイル", "FREESTYLE"),
            Map.entry("FREESTYLE", "FREESTYLE"),

            Map.entry("その他", "OTHER"),
            Map.entry("OTHER", "OTHER")
    );

    public Set<String> normalizeGenreCodes(List<String> rawGenres) {
        if (rawGenres == null || rawGenres.isEmpty()) {
            return Set.of();
        }

        Set<String> result = new LinkedHashSet<>();

        for (String rawGenre : rawGenres) {
            String normalized = normalizeOne(rawGenre);

            if (normalized == null) {
                continue;
            }

            if ("UNSET".equals(normalized)) {
                return Set.of();
            }

            result.add(normalized);
        }

        if (result.size() > MAX_GENRE_COUNT) {
            throw new IllegalArgumentException("ジャンルは最大" + MAX_GENRE_COUNT + "個まで選択できます");
        }

        return result;
    }

    private String normalizeOne(String rawGenre) {
        if (rawGenre == null) {
            return null;
        }

        String original = rawGenre.trim();

        if (original.isBlank()) {
            return null;
        }

        String normalized = Normalizer.normalize(original, Normalizer.Form.NFKC)
                .trim()
                .toUpperCase(Locale.ROOT);

        if ("未設定".equals(original) || "UNSET".equals(normalized) || "NONE".equals(normalized)) {
            return "UNSET";
        }

        return ALIASES.getOrDefault(normalized, normalized);
    }
}