package com.banword;

import java.util.List;

public class BanwordValidationResult {
    private final String originalSentence; // 원본 문장
    private final List<BanwordDetection> detectedBanwords; // 탐지된 금칙어 목록

    public BanwordValidationResult(String originalSentence, List<BanwordDetection> detectedBanwords, List<FilteredCharacter> filteredCharacters) {
        this.originalSentence = originalSentence;
        this.detectedBanwords = detectedBanwords;
    }

    public String getOriginalSentence() {
        return originalSentence;
    }

    public List<BanwordDetection> getDetectedBanwords() {
        return detectedBanwords;
    }
}
