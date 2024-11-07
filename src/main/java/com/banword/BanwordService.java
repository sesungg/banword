package com.banword;

import com.banword.annotation.EntityChangedEvent;
import org.ahocorasick.trie.PayloadEmit;
import org.ahocorasick.trie.PayloadTrie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BanwordService<T, U> {

    private PayloadTrie<T> banwordTrie;
    private PayloadTrie<U> allowWordTrie;

    @Autowired
    public BanwordService(BanwordConfigElement config, BanwordLoader loader) throws Exception {
        String banwordLocation = config.getBanwordLocation();
        String allowwordLocation = config.getAllowwordLocation();
        Class<T> banwordClass = (Class<T>) config.getBanwordClass();
        Class<U> allowwordClass = (Class<U>) config.getAllowwordClass();

        if (banwordLocation != null) {
            this.banwordTrie = buildTrie(banwordLocation, loader, banwordClass);
            this.allowWordTrie = buildTrie(allowwordLocation, loader, allowwordClass);
        }
    }

    private <V> PayloadTrie<V> buildTrie(String filePath, BanwordLoader loader, Class<V> clazz) throws Exception {
        List<V> words = loader.loadBanword(filePath).stream()
                .map(word -> createInstance(clazz, word))
                .collect(Collectors.toList());

        PayloadTrie.PayloadTrieBuilder<V> trieBuilder = PayloadTrie.<V>builder();
        words.forEach(word -> trieBuilder.addKeyword(word.toString(), word));

        return trieBuilder.build();
    }

    // 주기적 갱신
    @Scheduled(fixedRateString = "${banword.refresh.interval:60000}")
    public void scheduledRefresh() throws Exception {
        refreshBanwordTrie();
        refreshAllowWordTrie();
    }

    // 엔티티가 변경될 때 Trie 갱신
    // 수동으로 갱신하는 메서드
    public void refreshBanwordTrie() throws Exception {
        if (properties != null && loader != null) {
            this.banwordTrie = buildBanwordTrieFromFile(properties);
        }
    }
    public void refreshAllowWordTrie() throws Exception {
        if (properties != null && loader != null) {
            this.allowWordTrie = buildAllowWordTrieFromFile(properties);
        }
    }

    // Entity 변경 이벤트 처리기 (필요시에만 사용)
    @EventListener
    public void handleEntityChangedEvent(EntityChangedEvent event) throws Exception {
        if (event.isBanwordChanged()) {
            refreshBanwordTrie();
        }
        if (event.isAllowWordChanged()) {
            refreshAllowWordTrie();
        }
    }

    // 금칙어 Trie 생성 (파일 기반)
    private PayloadTrie<T> buildBanwordTrieFromFile(BanwordFilterProperties properties) throws Exception {
        List<T> banwords = loader.loadBanword(properties.getBanwordFilePath()).stream()
                .map(word -> createInstance(banwordClass, word))
                .collect(Collectors.toList());

        PayloadTrie.PayloadTrieBuilder<T> banwordTrieBuilder = PayloadTrie.<T>builder();
        banwords.forEach(banword -> banwordTrieBuilder.addKeyword(banword.toString(), banword));

        return banwordTrieBuilder.build();
    }

    // 허용 단어 Trie 생성 (파일 기반)
    private PayloadTrie<U> buildAllowWordTrieFromFile(BanwordFilterProperties properties) throws Exception {
        List<U> allowWords = loader.loadBanword(properties.getAllowwordFilePath()).stream()
                .map(word -> createInstance(allowwordClass, word))
                .collect(Collectors.toList());

        PayloadTrie.PayloadTrieBuilder<U> banwordTrieBuilder = PayloadTrie.<U>builder();
        allowWords.forEach(allowWord -> banwordTrieBuilder.addKeyword(allowWord.toString(), allowWord));

        return banwordTrieBuilder.build();
    }

    // 동적으로 금칙어 추가
    public void addBanwords(List<String> words) {
        PayloadTrie.PayloadTrieBuilder<T> banwordTrieBuilder = PayloadTrie.<T>builder();
        words.forEach(word -> banwordTrieBuilder.addKeyword(word, createInstance(banwordClass, word)));
        this.banwordTrie = banwordTrieBuilder.build();
    }

    // 동적으로 허용 단어 추가
    public void addAllowWords(List<String> words) {
        PayloadTrie.PayloadTrieBuilder<U> allowWordTrieBuilder = PayloadTrie.<U>builder();
        words.forEach(word -> allowWordTrieBuilder.addKeyword(word, createInstance(allowwordClass, word)));
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

    //리플렉션을 사용하여 인스턴스 생성하는 헬퍼 메서드
    private <V> V createInstance(Class<V> clazz, String keyword) {
        try {
            return clazz.getConstructor(String.class).newInstance(keyword);
        } catch (Exception e) {
            throw new RuntimeException("Filed to create instance of " + clazz.getName(), e);
        }
    }
}
