package com.banword.core;

import com.banword.enums.BanwordFilterPolicy;
import com.banword.model.FilteredCharacter;
import com.banword.model.FilteredResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BypassCharacterFilter {

    public FilteredResult filterBypassCharacters(String originSentence, Set<BanwordFilterPolicy> policies) {
        StringBuilder filteredSentence = new StringBuilder(originSentence.length());
        List<FilteredCharacter> filteredCharacters = new ArrayList<>();
        int[] originalToFilteredMap = new int[originSentence.length()];
        int[] filteredToOriginalMap = new int[originSentence.length()];

        int filteredPosition = 0;
        int filteredIndex = 0;

        for (int i = 0; i < originSentence.length(); i++) {
            char c = originSentence.charAt(i);

            // 우회 문자 필터링 및 맵핑
            if (isBypassCharacter(c, policies)) {
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
    private boolean isBypassCharacter(char c, Set<BanwordFilterPolicy> policies) {
        String combinedRegex = policies.stream()
                .map(BanwordFilterPolicy::getRegex)
                .reduce((a, b) -> a + "|" + b)
                .orElse("");
        return String.valueOf(c).matches(combinedRegex);
    }
}
