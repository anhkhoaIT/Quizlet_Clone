package com.example.cuoikiandroid.Phat;

public class Word {
    private String backText;
    private String frontText;

    public Word() {
    }

    public Word(String backText, String frontText) {
        this.backText = backText;
        this.frontText = frontText;
    }

    public String getBackText() {
        return backText;
    }

    public String getFrontText() {
        return frontText;
    }

    public void setBackText(String backText) {
        this.backText = backText;
    }

    public void setFrontText(String frontText) {
        this.frontText = frontText;
    }

}
