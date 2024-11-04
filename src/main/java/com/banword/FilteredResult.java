package com.banword;

import lombok.Getter;

import java.util.List;

public class FilteredResult {
    private final String filteredSentence;
    private final List<FilteredCharacter> filteredCharacters;

    public FilteredResult(String filteredSentence, List<FilteredCharacter> filteredCharacters) {
        this.filteredSentence = filteredSentence;
        this.filteredCharacters = filteredCharacters;
    }

    public String getFilteredSentence() {
        return filteredSentence;
    }

    public List<FilteredCharacter> getFilteredCharacters() {
        return filteredCharacters;
    }
}
