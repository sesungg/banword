package com.banword.core;

import com.banword.annotation.AllowWordField;
import com.banword.annotation.BanwordField;
import org.ahocorasick.trie.PayloadTrie;

import java.lang.reflect.Field;
import java.util.List;

public class TrieBuilder {

    public static <V> PayloadTrie<V> buildTrie(List<V> words) {
        PayloadTrie.PayloadTrieBuilder<V> trieBuilder = PayloadTrie.<V>builder();
        words.forEach(word -> trieBuilder.addKeyword(word.toString(), word));
        return trieBuilder.build();
    }

    // 금칙어 어노테이션 필드를 추적하여 빌드
    public static <T> PayloadTrie<T> buildBanwordTrie(List<T> words, Class<T> clazz) {
        // Trie 빌더 설정
        PayloadTrie.PayloadTrieBuilder<T> banwordTrieBuilder = PayloadTrie.<T>builder();
        Field banwordField = getBanwordField(clazz);

        // 각 객체의 금칙어 필드 값 추출 후 Trie에 추가
        for (T word : words) {
            try {
                String keyword = (String) banwordField.get(word);
                banwordTrieBuilder.addKeyword(keyword, word);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("금칙어 필드 접근 실패 : " + clazz.getName(), e);
            }
        }

        return banwordTrieBuilder.build();
    }

    // 허용단어 어노테이션 필드를 추적하여 빌드
    public static <U> PayloadTrie<U> buildAllowWordTrie(List<U> words, Class<U> clazz) {
        // Trie 빌더 설정
        PayloadTrie.PayloadTrieBuilder<U> banwordTrieBuilder = PayloadTrie.<U>builder();
        Field banwordField = getAllowWordField(clazz);

        // 각 객체의 금칙어 필드 값 추출 후 Trie에 추가
        for (U word : words) {
            try {
                String keyword = (String) banwordField.get(word);
                banwordTrieBuilder.addKeyword(keyword, word);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("허용단어 필드 접근 실패 : " + clazz.getName(), e);
            }
        }

        return banwordTrieBuilder.build();
    }

    // @BanwordField 어노테이션이 붙은 필드 탐색 메서드
    private static <T> Field getBanwordField(Class<T> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(BanwordField.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new IllegalArgumentException("@BanwordField 어노테이션이 붙은 금칙어 필드가 필요합니다.");
    }

    // @AllowWordField 어노테이션이 붙은 필드 탐색 메서드
    private static <U> Field getAllowWordField(Class<U> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(AllowWordField.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new IllegalArgumentException("@AllowWordField 어노테이션이 붙은 금칙어 필드가 필요합니다.");
    }

    public static <V> V instantiateFromKeyword(Class<V> clazz, String keyword) {
        try {
            return clazz.getConstructor(String.class).newInstance(keyword);
        } catch (Exception e) {
            throw new RuntimeException("Filed to create instance of " + clazz.getName(), e);
        }
    }
}
