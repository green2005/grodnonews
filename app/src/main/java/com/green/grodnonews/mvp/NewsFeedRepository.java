package com.green.grodnonews.mvp;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Handler;

import com.green.grodnonews.App;
import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.network.BlogDataSource;
import com.green.grodnonews.parser.FeedProcessor;
import com.green.grodnonews.room.NewsDB;
import com.green.grodnonews.room.NewsFeedItem;

import java.io.InputStream;
import java.util.List;

public class NewsFeedRepository implements NewsFeedContract.Repository {
    private NewsDB mDB;
    private BlogDataSource mSource;

    public NewsFeedRepository(Context appContext) {
        mSource = new BlogDataSource();
        mDB = ((App) appContext).getDB();
    }

    public void addObserver(FeedTypeEnum newsFeedTypeEnum, LifecycleOwner owner, Observer<List<NewsFeedItem>> feed) {
        mDB.newsFeedDao().getNewsFeedLiveData(newsFeedTypeEnum.getAsInt()).observe(owner, feed);
    }

    public void removerObserver(FeedTypeEnum newsFeedTypeEnum, LifecycleOwner owner) {
        mDB.newsFeedDao().getNewsFeedLiveData(newsFeedTypeEnum.getAsInt()).removeObservers(owner);
    }

    @Override
    public void fillFeed(final NewsFeedContract.Presenter presenter, final int start_from, final FeedTypeEnum newsFeedType) {
        final Handler h = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NewsFeedItem> newsFeedList = null;
                try {
                    InputStream stream = mSource.getFeedData(start_from, newsFeedType);
                    try {
                        newsFeedList = FeedProcessor.getFeedList(stream, newsFeedType, start_from);
                    } finally {
                        if (stream!=null){
			                stream.close();
                        }
                    }
                } catch (final Exception e) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            presenter.showError(e.getMessage());
                        }
                    });
                }
                if (newsFeedList != null) {
                    if (start_from == 0) {
                        mDB.newsFeedDao().clearNews(newsFeedType.getAsInt());
                    }
                    mDB.newsFeedDao().insertRecords(newsFeedList);
                }
            }
        }).start();
    }

}
