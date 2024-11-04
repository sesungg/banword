package com.banword;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BypassCharacterFilter {

    private static final String BYPASS_CHARACTER_PATTERN = "[^a-zA-Z가-힣\\s]";

    public FilteredResult filterBypassCharacters(String input) {
        // 우회 문자 목록을 저장할 리스트
        List<FilteredCharacter> filteredCharacters = new ArrayList<>();

        // 정규 표현식 패턴 컴파일
        Pattern pattern = Pattern.compile(BYPASS_CHARACTER_PATTERN);
        Matcher matcher = pattern.matcher(input);

        // 입력 문자열을 순회하며 우회 문자 찾기
        StringBuilder filteredSentenceBuilder = new StringBuilder();
        while (matcher.find()) {
            // 우회 문자 추가
            String bypassChar = matcher.group();
            filteredCharacters.add(new FilteredCharacter(bypassChar, matcher.start()));
        }

        // 우회 문자를 제외한 문자열 생성
        String filteredSentence = matcher.replaceAll("");

        return new FilteredResult(filteredSentence, filteredCharacters);
    }
}
