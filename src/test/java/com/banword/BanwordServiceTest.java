package com.banword;

import org.ahocorasick.trie.PayloadEmit;
import org.ahocorasick.trie.PayloadTrie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BanwordServiceTest {

    @Autowired
    private BanwordService banwordService;

    private PayloadTrie<Banword> banwordTrie;
    private PayloadTrie<AllowWord> allowWords;

    @BeforeEach
    void setBanword() {
        banwordTrie = PayloadTrie.<Banword>builder()
                .addKeyword("졸라", new Banword("졸라"))
                .addKeyword("존나", new Banword("존나"))
                .addKeyword("직거래", new Banword("직거래"))
                .addKeyword("쿠팡", new Banword("쿠팡"))
                .build();
    }

    @BeforeEach
    void setAllowWord() {
        allowWords = PayloadTrie.<AllowWord>builder()
                .addKeyword("고르곤졸라", new AllowWord("고르곤졸라"))
                .build();
    }

    @Test
    public void testContainsProhibitedWord() {
        String testString = "여기 고르곤졸라가 졸111라 존4232 234나게 맛있어요.";
        BanwordValidationResult result = banwordService.validate(testString);

        System.out.println("result.getOriginalSentence() = " + result.getOriginalSentence());
        for (BanwordDetection detectedBanword : result.getDetectedBanwords()) {
            System.out.println("=====================================================================");
            System.out.println("detectedBanword.getBanword() = " + detectedBanword.getBanword());
            System.out.println("detectedBanword.getStartPosition() = " + detectedBanword.getStartPosition());
            System.out.println("detectedBanword.getEndPosition() = " + detectedBanword.getEndPosition());
            System.out.println("detectedBanword.getLength() = " + detectedBanword.getLength());
        }
    }

}