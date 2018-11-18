package com.green.grodnonews;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.green.grodnonews.loader.Executor;
import com.green.grodnonews.loader.ImageLoader;
import com.green.grodnonews.mvp.NewsFeedContract;
import com.green.grodnonews.mvp.NewsFeedRepository;
import com.green.grodnonews.room.NewsDB;

public class App extends Application {
    private NewsFeedRepository mFeedRepository;
    private NewsDB mDB;

    private static Executor sExecutor;
    private static ImageLoader sImageLoader;
    private static String DB_NAME = "news_db";

    @Override
    public void onCreate() {
        super.onCreate();
        mDB = Room.databaseBuilder(this, NewsDB.class, DB_NAME)
                .fallbackToDestructiveMigration() //destroy any existing versions of db
                .build();

        mFeedRepository = new NewsFeedRepository(this);
        sExecutor = new Executor();
        sImageLoader = new ImageLoader(this);
    }

    public NewsFeedContract.Repository getRepository() {
        return mFeedRepository;
    }

    public static Object get(String key) {
        if (Executor.KEY.equalsIgnoreCase(key)) {
            return sExecutor;
        } else if (ImageLoader.KEY.equalsIgnoreCase(key)) {
            return sImageLoader;
        }
        return null;
    }

    public NewsDB getDB() {
        return mDB;
    }
}
