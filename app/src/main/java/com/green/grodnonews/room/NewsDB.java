package com.green.grodnonews.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {NewsFeedItem.class, NewsDetailItem.class}, version = 1)
public abstract class NewsDB extends RoomDatabase {
     public abstract NewsFeedDao newsFeedDao();
     public abstract NewsDetailDao newsDetailDao();
}
