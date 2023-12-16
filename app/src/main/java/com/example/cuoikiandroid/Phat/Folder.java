package com.example.cuoikiandroid.Phat;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Folder implements Parcelable {
    private String folderName;

    public Folder() {
    }

    public Folder(String folderName) {
        this.folderName = folderName;
    }

    protected Folder(Parcel in) {
        folderName = in.readString();
    }

    public static final Creator<Folder> CREATOR = new Creator<Folder>() {
        @Override
        public Folder createFromParcel(Parcel in) {
            return new Folder(in);
        }

        @Override
        public Folder[] newArray(int size) {
            return new Folder[size];
        }
    };

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(folderName);
    }
}
