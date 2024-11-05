package com.banword;

import org.ahocorasick.trie.PayloadEmit;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BanwordService<T, U> {

    private final GenericPayloadTrie<T> banwordTrie;
    private final GenericPayloadTrie<U> allowWordTrie;

    public BanwordService(BanwordFilterProperties properties, BanwordLoader loader) throws Exception {
        this(properties, loader, (Class<T>) Banword.class, (Class<U>) AllowWord.class);
    }

    public BanwordService(
            BanwordFilterProperties properties,
            BanwordLoader loader,
            Class<T> banwordClass,
            Class<U> allowwordClass) throws Exception {
        List<T> banwords = loader.loadBanword(properties.getBanwordFilePath()).stream()
                .map(keyword -> createInstance(banwordClass, keyword))
                .collect(Collectors.toList());
        List<U> allowwords = loader.loadBanword(properties.getAllowwordFilePath()).stream()
                .map(keyword -> createInstance(allowwordClass, keyword))
                .collect(Collectors.toList());

        this.banwordTrie = new GenericPayloadTrie<>(banwords, banword -> banword);
        this.allowWordTrie = new GenericPayloadTrie<>(allowwords, allowword -> allowword);
    }

    public BanwordValidationResult validate(String originSentence) {
        // Step 1: 우회 문자 분리
        FilteredResult filteredResult = new BypassCharacterFilter().filterBypassCharacters(originSentence);

        // Step 2: 금칙어 탐색
        Collection<PayloadEmit<T>> foundKeywords = banwordTrie.getTrie().parseText(filteredResult.getFilteredSentence());

        // Step 3: 허용 단어 탐색
        Collection<PayloadEmit<U>> detectedAllowWords = allowWordTrie.getTrie().parseText(filteredResult.getFilteredSentence());

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

    //리플렉션을 사용하여 인스턴스 생성하는 헬퍼 메서드
    private <V> V createInstance(Class<V> clazz, String keyword) {
        try {
            Constructor<V> constructor = clazz.getConstructor(String.class);
            return constructor.newInstance(keyword);
        } catch (Exception e) {
            throw new RuntimeException("Filed to create instance of " + clazz.getName(), e);
        }
    }
}
