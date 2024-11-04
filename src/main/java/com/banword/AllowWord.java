package com.banword;

public class AllowWord {
    private final String character;
    private final int startPosition;
    private final int endPosition;
    private final int length;

    public AllowWord(String character, int startPosition, int endPosition, int length) {
        this.character = character;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.length = length;
    }

    public String getCharacter() {
        return character;
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
