package com.example.cuoikiandroid.Hung;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
public class Flashcard implements Parcelable {
    private String FrontText;
    private String BackText;

    public Flashcard() {
    }

    public Flashcard(String frontText, String backText) {
        FrontText = frontText;
        BackText = backText;
    }

    public String getFrontText() {
        return FrontText;
    }

    public void setFrontText(String frontText) {
        FrontText = frontText;
    }

    public String getBackText() {
        return BackText;
    }

    public void setBackText(String backText) {
        BackText = backText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Flashcard (Parcel in) {
        BackText = in.readString();
        FrontText = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(BackText);
        dest.writeString(FrontText);
    }

    public static final Creator<Flashcard> CREATOR = new Creator<Flashcard>() {
        @Override
        public Flashcard createFromParcel(Parcel in) {
            return new Flashcard(in);
        }
        @Override
        public Flashcard[] newArray(int size) {
            return new Flashcard[size];
        }
    };
}
