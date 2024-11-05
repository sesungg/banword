package com.banword;

import org.ahocorasick.trie.PayloadEmit;
import org.ahocorasick.trie.PayloadTrie;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class BanwordService {
    private final PayloadTrie<Banword> banwordTrie;
    private final AllowWordTrie allowWordTrie;

    public BanwordService() {
        banwordTrie = PayloadTrie.<Banword>builder()
                .addKeyword("졸라", new Banword("졸라"))
                .addKeyword("존나", new Banword("존나"))
                .addKeyword("직거래", new Banword("직거래"))
                .addKeyword("쿠팡", new Banword("쿠팡"))
                .build();

        List<AllowWord> allowWords = new ArrayList<>();
        allowWords.add(new AllowWord("고르곤졸라", 0, 0, 5));
        allowWordTrie = new AllowWordTrie(allowWords);
    }

    public BanwordValidationResult validate(String originSentence) {
        // Step 1: 우회 문자 분리
        FilteredResult filteredResult = new BypassCharacterFilter().filterBypassCharacters(originSentence);

        // Step 2: 금칙어 탐색
        Collection<PayloadEmit<Banword>> foundKeywords = banwordTrie.parseText(filteredResult.getFilteredSentence());

        // Step 3: 허용 단어 탐색
        List<AllowWord> detectedAllowWords = allowWordTrie.searchAllowWords(filteredResult.getFilteredSentence());

        // Step 4: 허용 단어에 포함되는 금칙어 제외
        List<BanwordDetection> detectedBanwords = new ArrayList<>();
        for (PayloadEmit<Banword> foundKeyword : foundKeywords) {
            String keyword = foundKeyword.getKeyword();
            int filteredStart = foundKeyword.getStart();
            int filteredEnd = foundKeyword.getEnd();

            // 원본 문장 위치로 변환
            int originalStart = filteredResult.mapToOriginalPosition(filteredStart);
            int originalEnd = filteredResult.mapToOriginalPosition(filteredEnd);

            boolean isOverlapping = false;
            for (AllowWord allowWord : detectedAllowWords) {
                // 금칙어와 허용 단어의 위치가 겹치는지 확인
                // 금칙어가 허용 단어의 범위 내에 포함되는 경우
                if (allowWord.getStartPosition() <= filteredStart && filteredEnd <= allowWord.getEndPosition()) {
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
