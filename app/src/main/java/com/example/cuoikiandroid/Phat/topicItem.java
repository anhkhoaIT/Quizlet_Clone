package com.example.cuoikiandroid.Phat;

public class topicItem {
    private String topicName;
    private int wordAmount;
    private String term;
    private String definition;

    public topicItem(String topicName, int wordAmount, String term, String definition) {
        this.topicName = topicName;
        this.wordAmount = wordAmount;
        this.term = term;
        this.definition = definition;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getWordAmount() {
        return wordAmount;
    }

    public void setWordAmount(int wordAmount) {
        this.wordAmount = wordAmount;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
