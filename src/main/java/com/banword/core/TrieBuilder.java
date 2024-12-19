package com.banword.core;

import org.ahocorasick.trie.PayloadTrie;

import java.util.List;
import java.util.function.Function;

public class TrieBuilder {

    public static <V> PayloadTrie<V> buildTrie(List<V> words, Function<V, String> keywordExtractor) {
        PayloadTrie.PayloadTrieBuilder<V> trieBuilder = PayloadTrie.<V>builder();
        words.forEach(word -> trieBuilder.addKeyword(keywordExtractor.apply(word), word));
        return trieBuilder.build();
    }
}
