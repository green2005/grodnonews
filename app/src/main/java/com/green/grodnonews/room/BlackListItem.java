package com.green.grodnonews.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "blackLst")
public class BlackListItem implements Parcelable {
    public BlackListItem(String userName) {
        this.userName = userName;
    }

    public BlackListItem(Parcel parcel) {
        userName = parcel.readString();
    }

    @PrimaryKey(autoGenerate = true)
    public int uniqueId;
    public String userName;

    public static final Parcelable.Creator<BlackListItem> CREATOR = new Parcelable.Creator<BlackListItem>() {

        @Override
        public BlackListItem createFromParcel(Parcel source) {
            return new BlackListItem(source);
        }

        @Override
        public BlackListItem[] newArray(int size) {
            return new BlackListItem[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
    }
}
