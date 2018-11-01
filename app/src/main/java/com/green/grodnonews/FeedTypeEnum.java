package com.green.grodnonews;

import android.arch.persistence.room.PrimaryKey;

import java.security.PublicKey;

public enum FeedTypeEnum {

    S13(0, "http://s13.ru", "S13.ru"), HRODNALIFE(2, "https://hrodna.life", "Hrodna.life"), NEWGRODNO(1, "https://newgrodno.by", "NewGrodno.by");

    private int mId;
    private String mHost;
    private String mTitle;

    FeedTypeEnum(int i, String host, String title) {
        mId = i;
        mHost = host;
        mTitle = title;
    }

    public int getAsInt() {
        return mId;
    }

    public String getHost() {
        return mHost;
    }

    public String getTitle() {
        return mTitle;
    }

    public static FeedTypeEnum getTypeById(int id) {
        FeedTypeEnum[] items = FeedTypeEnum.values();
        for (FeedTypeEnum item : items) {
            if (item.getAsInt() == id) {
                return item;
            }
        }
        return null;
    }
}
