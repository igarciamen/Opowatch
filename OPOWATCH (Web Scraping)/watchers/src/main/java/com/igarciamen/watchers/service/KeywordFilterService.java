package com.igarciamen.watchers.service;

import com.igarciamen.watchers.dto.ScrapedPosting;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class KeywordFilterService {

    public List<String> parseKeywords(String keywordsCsv) {
        if (keywordsCsv == null || keywordsCsv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(keywordsCsv.split(","))
                .map(String::trim)
                .filter(keyword -> !keyword.isEmpty())
                .map(this::normalize)
                .toList();
    }

    public boolean matchesKeywords(String title, List<String> normalizedKeywords) {
        if (title == null || title.isBlank() || normalizedKeywords.isEmpty()) {
            return false;
        }
        String normalizedTitle = normalize(title);
        return normalizedKeywords.stream().anyMatch(normalizedTitle::contains);
    }

    public List<ScrapedPosting> filter(List<ScrapedPosting> postings, String keywordsCsv) {
        List<String> normalizedKeywords = parseKeywords(keywordsCsv);
        return postings.stream()
                .filter(posting -> matchesKeywords(posting.getTitle(), normalizedKeywords))
                .toList();
    }

    private String normalize(String text) {
        String withoutAccents = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase(Locale.ROOT);
    }
}