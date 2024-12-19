package com.banword.service;

import com.banword.core.BanwordDetection;
import com.banword.core.BypassCharacterFilter;
import com.banword.core.TrieBuilder;
import com.banword.enums.BanwordFilterPolicy;
import com.banword.model.Allowword;
import com.banword.model.Banword;
import com.banword.model.BanwordValidationResult;
import com.banword.model.FilteredResult;
import com.banword.provider.AllowwordProvider;
import com.banword.provider.BanwordProvider;
import org.ahocorasick.trie.PayloadEmit;
import org.ahocorasick.trie.PayloadTrie;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
public class BanwordValidator {

    private PayloadTrie<Banword> banwordTrie;
    private PayloadTrie<Allowword> allowwordTrie;

    public BanwordValidator(BanwordProvider banwordProvider, AllowwordProvider allowwordProvider) {
        this.banwordTrie = TrieBuilder.buildTrie(banwordProvider.provideBanword(), Banword::getWord);
        this.allowwordTrie = TrieBuilder.buildTrie(allowwordProvider.provideAllowwords(), Allowword::getWord);
    }

    protected synchronized void rebuildTries(List<Banword> newBanwords, List<Allowword> newAllowwords) {
        this.banwordTrie = TrieBuilder.buildTrie(newBanwords, Banword::getWord);
        this.allowwordTrie = TrieBuilder.buildTrie(newAllowwords, Allowword::getWord);
        // todo: 관리자에서 금칙어, 허용단어가 추가되었을 경우 고려하여 추가 구현 필요
    }

    // 검증 로직
    public BanwordValidationResult validate(String originSentence, Set<BanwordFilterPolicy> policies) {
        // Step 1: 우회 문자 분리
        FilteredResult filteredResult = new BypassCharacterFilter().filterBypassCharacters(originSentence, policies);

        // Step 2: 금칙어 탐색
        Collection<PayloadEmit<Banword>> foundKeywords = banwordTrie.parseText(filteredResult.getFilteredSentence());

        // Step 3: 허용 단어 탐색
        Collection<PayloadEmit<Allowword>> detectedAllowWords = allowwordTrie.parseText(filteredResult.getFilteredSentence());

        // Step 4: 허용 단어에 포함되는 금칙어 제외
        List<BanwordDetection> detectedBanwords = new ArrayList<>();
        for (PayloadEmit<Banword> foundKeyword : foundKeywords) {
            int filteredStart = foundKeyword.getStart();
            int filteredEnd = foundKeyword.getEnd();

            // 원본 문장 위치로 변환
            int originalStart = filteredResult.mapToOriginalPosition(filteredStart);
            int originalEnd = filteredResult.mapToOriginalPosition(filteredEnd);

            boolean isOverlapping = false;
            for (PayloadEmit<Allowword> allowWord : detectedAllowWords) {
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

        return new BanwordValidationResult(originSentence, detectedBanwords);
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
