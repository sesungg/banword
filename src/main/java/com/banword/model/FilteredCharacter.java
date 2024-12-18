package com.banword.model;

public class FilteredCharacter {
    private final String character;
    private final int position;

    public FilteredCharacter(String character, int position) {
        this.character = character;
        this.position = position;
    }

    public String getCharacter() {
        return character;
    }

    public int getPosition() {
        return position;
    }
}
