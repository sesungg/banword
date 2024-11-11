package com.banword;

import com.banword.annotation.AllowWordField;
import com.banword.annotation.BanwordField;
import org.ahocorasick.trie.PayloadEmit;
import org.ahocorasick.trie.PayloadTrie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
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
    private BanwordFilterProperties properties;

    @Autowired
    public BanwordValidator(@Autowired(required = false) BanwordConfigElement config, BanwordLoader loader, BanwordFilterProperties properties) throws Exception {
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
            List<T> banwords = (List<T>) loader.loadBanword(banwordLocation);
            this.banwordTrie = TrieBuilder.buildTrie(banwords, banwordClass);
        }
        if (allowwordLocation != null) {
            List<U> allowWords = (List<U>) loader.loadBanword(allowwordLocation);
            this.allowWordTrie = TrieBuilder.buildTrie(allowWords, allowwordClass);
        }
    }

    // 동적으로 금칙어 추가
    public void addBanwords(List<String> words) {
        if (banwordClass == null) this.banwordClass = (Class<T>) Banword.class;
        List<T> banwords = words.stream()
                .map(word -> TrieBuilder.createInstance(banwordClass, word))
                .collect(Collectors.toList());
        this.banwordTrie = TrieBuilder.buildTrie(banwords, banwordClass);
    }

    // 금칙어 추가 및 클래스 변경
    public void addBanwords(List<T> words, Class<T> clazz) {
        // banwordClass 필드 설정
        this.banwordClass = clazz;

        // Trie 빌더 설정
        PayloadTrie.PayloadTrieBuilder<T> banwordTrieBuilder = PayloadTrie.<T>builder();

        // 리플랙션으로 banword 필드 추출
        Field banwordField = null;

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(BanwordField.class)) {
                banwordField = field;
                banwordField.setAccessible(true);
                break;
            }
        }

        if (banwordField == null) {
            throw new IllegalArgumentException("금칙어 필드가 설정되지 않았습니다. @BanwordField 어노테이션을 추가해 주세요.");
        }

        // 각 객체의 banword 필드를 키로 추가
        for (T word : words) {
            try {
                String keyword = (String) banwordField.get(word);
                banwordTrieBuilder.addKeyword(keyword, word);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("금칙어 필드 접근 실패 : " + clazz.getName(), e);
            }
        }

        this.banwordTrie = banwordTrieBuilder.build();
    }

    // 동적으로 허용 단어 추가
    public void addAllowWords(List<String> words) {
        if (allowwordClass == null) this.allowwordClass = (Class<U>) AllowWord.class;
        List<U> allowwords = words.stream()
                .map(word -> TrieBuilder.createInstance(allowwordClass, word))
                .collect(Collectors.toList());
        this.allowWordTrie = TrieBuilder.buildTrie(allowwords, allowwordClass);
    }

    // 허용 단어 추가 및 클래스 변경
    public void addAllowWords(List<U> words, Class<U> clazz) {
        this.allowwordClass = clazz;

        PayloadTrie.PayloadTrieBuilder<U> allowWordTrieBuilder = PayloadTrie.<U>builder();

        Field allowWordField = null;

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(AllowWordField.class)) {
                allowWordField = field;
                allowWordField.setAccessible(true);
                break;
            }
        }

        if (allowWordField == null) {
            throw new IllegalArgumentException("금칙어 필드가 설정되지 않았습니다. @AllowWordField 어노테이션를 추가해 주세요.");
        }

        for (U word : words) {
            try {
                String keyword = (String) allowWordField.get(word);
                allowWordTrieBuilder.addKeyword(keyword, word);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("금칙어 필드 접근 실패: " + clazz.getName(), e);
            }
        }

        this.allowWordTrie = allowWordTrieBuilder.build();
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
