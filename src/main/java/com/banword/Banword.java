package com.banword;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Banword {
    private String word;

    public Banword(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
