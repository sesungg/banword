package com.banword;

import org.ahocorasick.trie.PayloadEmit;
import org.ahocorasick.trie.PayloadTrie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AllowWordTrie {
    private final PayloadTrie<AllowWord> allowWordTrie;

    public AllowWordTrie(List<AllowWord> allowWords) {
        PayloadTrie.PayloadTrieBuilder<AllowWord> builder = PayloadTrie.builder();
        for (AllowWord allowWord : allowWords) {
            builder.addKeyword(allowWord.getCharacter(), allowWord);
        }
        this.allowWordTrie = builder.build();
    }

    public List<AllowWord> searchAllowWords(String input) {
        Collection<PayloadEmit<AllowWord>> foundAllowWords = allowWordTrie.parseText(input);
        List<AllowWord> result = new ArrayList<>();
        for (PayloadEmit<AllowWord> emit : foundAllowWords) {
            result.add(new AllowWord(emit.getKeyword(), emit.getStart(), emit.getEnd(), emit.size()));
        }
        return result;
    }
}
