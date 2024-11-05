package com.banword;

import org.ahocorasick.trie.PayloadTrie;

import java.util.List;

public class GenericPayloadTrie<T> {
    private final PayloadTrie<T> trie;

    public GenericPayloadTrie(List<T> words, PayloadMapper<T> mapper) {
        PayloadTrie.PayloadTrieBuilder<T> builder = PayloadTrie.builder();
        words.forEach(word -> builder.addKeyword(word.toString(), mapper.map(word)));
        this.trie = builder.build();
    }

    public PayloadTrie<T> getTrie() {
        return trie;
    }

    public interface PayloadMapper<T> {
        T map(T word);


    }
}
