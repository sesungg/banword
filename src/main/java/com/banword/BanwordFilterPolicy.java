package com.banword;

import lombok.Getter;

@Getter
public enum BanwordFilterPolicy {
    NUMBERS("[\\p{N}]"),
    WHITESPACES("[\\s]"),
    FOREIGNLANGUAGES("[\\P{l}&&[^ㄱ-ㅎ가-힣ㅏ-ㅣa-zA-Z]]");

    private final String regex;

    BanwordFilterPolicy(String regex) {
        this.regex = regex;
    }
}
