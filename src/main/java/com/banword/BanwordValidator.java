package com.banword;

import org.ahocorasick.trie.PayloadEmit;
import org.ahocorasick.trie.PayloadTrie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BanwordValidator<T, U> {

    private PayloadTrie<T> banwordTrie;
    private PayloadTrie<U> allowWordTrie;
    private Class<T> banwordClass;
    private Class<U> allowwordClass;
    private BanwordLoader loader;
    private BanwordConfigElement config;

    @Autowired
    public BanwordValidator(@Autowired(required = false) BanwordConfigElement config, BanwordLoader loader) throws Exception {
        this.loader = loader;
        this.config = config;

        initTries();
    }

    private void initTries() throws Exception {
        this.banwordClass = (Class<T>) config.getBanwordClass();
        this.allowwordClass = (Class<U>) config.getAllowwordClass();
        String banwordLocation = config.getBanwordLocation();
        String allowwordLocation = config.getAllowwordLocation();

        if (banwordLocation != null) {
            List<T> banwords = loader.loadBanword(banwordLocation).stream()
                    .map(word -> TrieBuilder.instantiateFromKeyword(banwordClass, word))
                    .collect(Collectors.toList());
            this.banwordTrie = TrieBuilder.buildTrie(banwords);
        }

        if (allowwordLocation != null) {
            List<U> allowWords = loader.loadBanword(allowwordLocation).stream()
                    .map(word -> TrieBuilder.instantiateFromKeyword(allowwordClass, word))
                    .collect(Collectors.toList());
            this.allowWordTrie = TrieBuilder.buildTrie(allowWords);
        }
    }

    public void addBanword(List<T> words) {
        this.banwordTrie = TrieBuilder.buildBanwordTrie(words, banwordClass);
    }

    // 금칙어 리스트를 추가하는 메서드
    public void addBanword(List<T> words, Class<T> clazz) {
        this.banwordTrie = TrieBuilder.buildBanwordTrie(words, clazz);
    }

    public void addAllowWord(List<U> words) {
        this.allowWordTrie = TrieBuilder.buildAllowWordTrie(words, allowwordClass);
    }

    public void addAllowWord(List<U> words, Class<U> clazz) {
        this.allowWordTrie = TrieBuilder.buildAllowWordTrie(words, clazz);
    }

    // 검증 로직
    public BanwordValidationResult validate(String originSentence) {
        // Step 1: 우회 문자 분리
        FilteredResult filteredResult = new BypassCharacterFilter().filterBypassCharacters(originSentence);

        // Step 2: 금칙어 탐색
        Collection<PayloadEmit<T>> foundKeywords = banwordTrie.parseText(filteredResult.getFilteredSentence());

        // Step 3: 허용 단어 탐색
        Collection<PayloadEmit<U>> detectedAllowWords = allowWordTrie.parseText(filteredResult.getFilteredSentence());

        // Step 4: 허용 단어에 포함되는 금칙어 제외
        List<BanwordDetection> detectedBanwords = new ArrayList<>();
        for (PayloadEmit<T> foundKeyword : foundKeywords) {
            int filteredStart = foundKeyword.getStart();
            int filteredEnd = foundKeyword.getEnd();

            // 원본 문장 위치로 변환
            int originalStart = filteredResult.mapToOriginalPosition(filteredStart);
            int originalEnd = filteredResult.mapToOriginalPosition(filteredEnd);

            boolean isOverlapping = false;
            for (PayloadEmit<U> allowWord : detectedAllowWords) {
                // 금칙어와 허용 단어의 위치가 겹치는지 확인
                // 금칙어가 허용 단어의 범위 내에 포함되는 경우
                if (allowWord.getStart() <= filteredStart && filteredEnd <= allowWord.getEnd()) {
                    isOverlapping = true;
                    break;
                }
            }

            // 겹치지 않는 금칙어만 추가
            if (!isOverlapping) {
                String originalBanword = reconstructOriginalBanword(originSentence, originalStart, originalEnd);
                detectedBanwords.add(new BanwordDetection(originalBanword, originalStart, originalEnd, originalBanword.length()));
            }
        }

        return new BanwordValidationResult(originSentence, detectedBanwords, filteredResult.getFilteredCharacters());
    }

    // Helper 메서드 : 원본 금칙어 재조합
    private String reconstructOriginalBanword(String originSentence, int start, int end) {
        StringBuilder originalBanword = new StringBuilder();
        for (int i = start; i <= end; i++) {
            originalBanword.append(originSentence.charAt(i));
        }
        return originalBanword.toString();
    }
}
