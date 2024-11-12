package com.banword;

import java.util.ArrayList;
import java.util.List;

public class BypassCharacterFilter {

    public FilteredResult filterBypassCharacters(String originSentence) {
        StringBuilder filteredSentence = new StringBuilder(originSentence.length());
        List<FilteredCharacter> filteredCharacters = new ArrayList<>();
        int[] originalToFilteredMap = new int[originSentence.length()];
        int[] filteredToOriginalMap = new int[originSentence.length()];

        int filteredPosition = 0;
        int filteredIndex = 0;

        for (int i = 0; i < originSentence.length(); i++) {
            char c = originSentence.charAt(i);

            // 우회 문자 필터링 및 맵핑
            if (isBypassCharacter(c)) {
                filteredCharacters.add(new FilteredCharacter(String.valueOf(c), i));
                originalToFilteredMap[i] = -1; // 우회 문자는 -1로 매핑
            } else {
                // 필터링된 문자를 추가하고 매핑 배열 업데이트
                filteredSentence.append(c);
                originalToFilteredMap[i] = filteredPosition;
                filteredToOriginalMap[filteredIndex++] = i;
                filteredPosition++;
            }
        }

        int[] finalFilteredToOriginalMap = new int[filteredIndex];
        System.arraycopy(filteredToOriginalMap, 0, finalFilteredToOriginalMap, 0, filteredIndex);

        // FilteredResult 객체 생성
        return new FilteredResult(
                filteredSentence.toString(),
                originSentence,
                filteredCharacters,
                originalToFilteredMap,
                finalFilteredToOriginalMap
        );
    }

    // 알파벳, 한글, 숫자 이외의 문자를 우회 문자로 간주
    private boolean isBypassCharacter(char c) {
        return !((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
                (c >= '가' && c <= '힣') || Character.isWhitespace(c));
    }
}
