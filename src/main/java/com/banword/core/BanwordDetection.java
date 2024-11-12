package com.banword.core;

public class BanwordDetection {
    private final String banword;
    private final int startPosition;
    private final int endPosition;
    private final int length;

    public BanwordDetection(String banword, int startPosition, int endPosition, int length) {
        this.banword = banword;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.length = length;
    }

    public String getBanword() {
        return banword;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public int getLength() {
        return length;
    }
}
