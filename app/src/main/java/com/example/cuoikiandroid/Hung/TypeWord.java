package com.example.cuoikiandroid.Hung;

public class TypeWord {

    private String question;
    private String typing;

    public TypeWord() {
    }

    public TypeWord(String question, String typing) {
        this.question = question;
        this.typing = typing;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTyping() {
        return typing;
    }

    public void setTyping(String typing) {
        this.typing = typing;
    }
}
