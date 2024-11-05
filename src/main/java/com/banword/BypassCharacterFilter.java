package com.banword;

import java.util.ArrayList;
import java.util.List;

public class BypassCharacterFilter {

    private static final String BYPASS_CHARACTER_PATTERN = "[^a-zA-Z가-힣\\s]";

    public FilteredResult filterBypassCharacters(String originSentence) {
        StringBuilder filteredSentence = new StringBuilder();
        List<FilteredCharacter> filteredCharacters = new ArrayList<>();
        int[] originalToFilteredMap = new int[originSentence.length()];
        List<Integer> filteredToOriginalList = new ArrayList<>();

        int filteredPosition = 0;

        for (int i = 0; i < originSentence.length(); i++) {
            char c = originSentence.charAt(i);

            if (Character.isWhitespace(c)) continue;

            if (isBypassCharacter(c)) {
                // 우회 문자일 경우
                filteredCharacters.add(new FilteredCharacter(String.valueOf(c), i));
                originalToFilteredMap[i] = -1;
            } else {
                // 필터링된 문자일 경우
                filteredSentence.append(c);
                filteredToOriginalList.add(i);
                originalToFilteredMap[i] = filteredPosition;
                filteredPosition++;
            }
        }
        int[] filteredToOriginalMap = filteredToOriginalList.stream().mapToInt(Integer::intValue).toArray();

        // FilteredResult 객체 생성
        return new FilteredResult(
                filteredSentence.toString(),
                originSentence,
                filteredCharacters,
                originalToFilteredMap,
                filteredToOriginalMap
        );
    }

    private boolean isBypassCharacter(char c) {
        // 숫자이거나 한글/알파벳이 아닌 문자만 우회 문자로 간주
        return Character.isDigit(c) || !(Character.isLetter(c) || isKoreanCharacter(c));
    }

    private boolean isKoreanCharacter(char c) {
        // 한글 유니코드 범위를 통해 한글 문자인지 확인
        return (c >= '가' && c <= '힣');
    }
}
