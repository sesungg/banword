package com.banword;

import java.util.List;

public class FilteredResult {
    private final String filteredSentence;
    private final String originalSentence;
    private final List<FilteredCharacter> filteredCharacters;
    private final int[] originalToFilteredMap;
    private final int[] filteredToOriginalMap;

    public FilteredResult(String filteredSentence, String originalSentence, List<FilteredCharacter> filteredCharacters, int[] originalToFilteredMap, int[] filteredToOriginalMap) {
        this.filteredSentence = filteredSentence;
        this.originalSentence = originalSentence;
        this.filteredCharacters = filteredCharacters;
        this.originalToFilteredMap = originalToFilteredMap;
        this.filteredToOriginalMap = filteredToOriginalMap;
    }

    public String getFilteredSentence() {
        return filteredSentence;
    }

    public List<FilteredCharacter> getFilteredCharacters() {
        return filteredCharacters;
    }

    // 필터링된 위치 -> 원본 위치 변환 후 해당 문자의 반환
    public char getOriginalCharacter(int filteredPosition) {
        int originalPosition = mapToOriginalPosition(filteredPosition);
        return (originalPosition != -1) ? originalSentence.charAt(originalPosition) : '\0'; // 유효하지 않은 경우 빈 문자 반환
    }

    public int mapToOriginalPosition(int filteredPosition) {
        return (filteredPosition >= 0 && filteredPosition < filteredToOriginalMap.length) ? filteredToOriginalMap[filteredPosition] : -1;
    }

    public int mapToFilteredPosition(int originalPosition) {
        return (originalPosition >= 0 && originalPosition < originalToFilteredMap.length) ? originalToFilteredMap[originalPosition] : -1;
    }

    public int[] getOriginalToFilteredMap() {
        return originalToFilteredMap;
    }

    public int[] getFilteredToOriginalMap() {
        return filteredToOriginalMap;
    }
}
