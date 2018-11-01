package com.green.grodnonews.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "newsfeed")
public class NewsFeedItem implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int uniqueId;
    public String id;
    public String title;
    public String imgUrl;
    public String content;
    public int feedSourceId;
    public String date;

    public String url;
    public String text;
    public int imageWidth;
    public int imageHeigt;
    public int offset;

    public NewsFeedItem(Parcel source) {
        this.id = source.readString();
        url = source.readString();
        title = source.readString();
        imgUrl = source.readString();
        imageHeigt = source.readInt();
        imageWidth = source.readInt();
        feedSourceId = source.readInt();
    }

    public NewsFeedItem() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<NewsFeedItem> CREATOR = new Parcelable.Creator<NewsFeedItem>() {

        @Override
        public NewsFeedItem createFromParcel(Parcel source) {
            return new NewsFeedItem(source);
        }

        @Override
        public NewsFeedItem[] newArray(int size) {
            return new NewsFeedItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(imgUrl);
        dest.writeInt(imageHeigt);
        dest.writeInt(imageWidth);
        dest.writeInt(feedSourceId);
    }
}
