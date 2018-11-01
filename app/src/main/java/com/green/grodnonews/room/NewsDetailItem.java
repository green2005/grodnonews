package com.green.grodnonews.room;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "newsDetail")
public class NewsDetailItem {
    @PrimaryKey(autoGenerate = true)
    public int uniqueId;
    public String articleUrl;
    public String itemUrl;
    public int itemType;
    public String content;
    public String date;
    public String commentId;
    public String akismet;
    public String ak_js;
    public String width;
    public String height;
    public String authorName;
    public String authorImage;
    public String karmaUp;
    public String karmaDown;
}
