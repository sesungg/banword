package com.banword.enums;

public enum BanwordFilterPolicy {

    NUMBERS("[\\p{N}]"),
    WHITESPACES("[\\s]"),
    FOREIGNLANGUAGES("[\\p{L}&&[^ㄱ-ㅎ가-힣ㅏ-ㅣa-zA-Z]]");

    private final String regex;

    BanwordFilterPolicy(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}
