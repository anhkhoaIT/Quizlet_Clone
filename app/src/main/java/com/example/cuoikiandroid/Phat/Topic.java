package com.example.cuoikiandroid.Phat;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Topic implements Parcelable {
    private String topicName;
    private String wordAmount;

    public Topic() {
    }

    public Topic(String topicName, String wordAmount) {
        this.topicName = topicName;
        this.wordAmount = wordAmount;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getWordAmount() {
        return wordAmount;
    }

    public void setWordAmount(String wordAmount) {
        this.wordAmount = wordAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Topic (Parcel in) {
        topicName = in.readString();
        wordAmount = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(topicName);
        dest.writeString(wordAmount);
    }

    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }
        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };
}
