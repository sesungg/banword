package com.banword;

import org.ahocorasick.trie.PayloadEmit;
import org.ahocorasick.trie.PayloadTrie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BanwordServiceTest {

    @Autowired
    private BanwordService banwordService;

    @Test
    public void testContainsProhibitedWord() {
        String testString = "여기 고르곤졸라가 졸111라 맛있어요.";
        BanwordValidationResult result = banwordService.validate(testString);

        System.out.println("result.getOriginalSentence() = " + result.getOriginalSentence());
        for (BanwordDetection detectedBanword : result.getDetectedBanwords()) {
            System.out.println("detectedBanword.getBanword() = " + detectedBanword.getBanword());
            System.out.println("detectedBanword.getStartPosition() = " + detectedBanword.getStartPosition());
            System.out.println("detectedBanword.getEndPosition() = " + detectedBanword.getEndPosition());
            System.out.println("detectedBanword.getLength() = " + detectedBanword.getLength());
        }
    }

}