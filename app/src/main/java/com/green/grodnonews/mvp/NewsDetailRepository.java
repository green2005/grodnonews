package com.green.grodnonews.mvp;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Handler;

import com.green.grodnonews.App;
import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.blogio.AccountSettings;
import com.green.grodnonews.network.NetworkDataSource;
import com.green.grodnonews.network.S13DataSource;
import com.green.grodnonews.parser.DetailProcessor;
import com.green.grodnonews.parser.FeedProcessor;
import com.green.grodnonews.room.BlackListItem;
import com.green.grodnonews.room.NewsDB;
import com.green.grodnonews.room.NewsDetailItem;

import java.io.InputStream;
import java.util.List;


public class NewsDetailRepository implements NewsDetailContract.Repository {
    private NewsDB mDB;
    private NetworkDataSource mSource;

    NewsDetailRepository(Context appContext, FeedTypeEnum feedType) {
        mDB = ((App) appContext).getDB();
        mSource = NetworkDataSource.getNetworkDataSource(appContext, feedType);
    }

    public void addObserver(String url, LifecycleOwner owner, Observer<List<NewsDetailItem>> observer) {
        mDB.newsDetailDao().getNewsDetail(url).observe(owner, observer);
    }

    public void removeObserver(String url, LifecycleOwner owner) {
        mDB.newsDetailDao().getNewsDetail(url).removeObservers(owner);
    }

    public void setAccountSettings(AccountSettings settings) {
        if (mSource instanceof S13DataSource) {
            ((S13DataSource) (mSource)).setAccountSettings(settings);
        }
    }

    @Override
    public void requestData(final NewsDetailContract.Presenter presenter, final String url, final FeedTypeEnum feedType) {
        final Handler h = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream stream = null;
                    String response = null;
                    try {
                        stream = mSource.getDetailData(url);
                        response = FeedProcessor.getStringFromStream(stream, "");
                    } finally {
                        if (stream != null) {
                            stream.close();
                        }
                    }
                    List<NewsDetailItem> items = DetailProcessor.getDetailItems(url, response, feedType);
                    mDB.newsDetailDao().deleteAllRecords();
                    mDB.newsDetailDao().insertRecords(items);

                } catch (final Exception e) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            presenter.processError(e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void addUserToBlackList(final String userName, NewsDetailContract.Presenter presenter, String url, FeedTypeEnum feedType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDB.blackListDao().insertRecord(new BlackListItem(userName));
            }
        }).start();
        requestData(presenter, url, feedType);
    }

}
