package com.banword;

import org.ahocorasick.trie.PayloadTrie;

import java.util.List;

public class TrieBuilder {

    public static <V> PayloadTrie<V> buildTrie(List<V> words, Class<V> clazz) {
        PayloadTrie.PayloadTrieBuilder<V> trieBuilder = PayloadTrie.<V>builder();
        words.forEach(word -> trieBuilder.addKeyword(word.toString(), word));
        return trieBuilder.build();
    }

    public static <V> V createInstance(Class<V> clazz, String keyword) {
        try {
            return clazz.getConstructor(String.class).newInstance(keyword);
        } catch (Exception e) {
            throw new RuntimeException("Filed to create instance of " + clazz.getName(), e);
        }
    }
}
