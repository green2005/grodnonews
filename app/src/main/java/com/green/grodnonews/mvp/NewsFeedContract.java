package com.green.grodnonews.mvp;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;

import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.room.NewsFeedItem;

import java.util.List;

public interface NewsFeedContract {
    interface View {
        void showError(String errorText);

        LifecycleOwner getLifecycleOwner();

        Observer<List<NewsFeedItem>> getObserver();

        FeedTypeEnum getFeedType();
    }

    interface Presenter {
        void onViewRequestFeedStart(NewsFeedContract.View contractView, Context context);
        void onViewRequestFeedNext(NewsFeedContract.View contractView, Context context);


        void onViewCreated(NewsFeedContract.View contractView, Context context);
        void onViewDestroyed(NewsFeedContract.View contractView);
        void showError(String error);
    }

    interface Repository {
        void addObserver(FeedTypeEnum newsFeedTypeEnum, LifecycleOwner owner, Observer<List<NewsFeedItem>> feed);

        void removerObserver(FeedTypeEnum newsFeedTypeEnum, LifecycleOwner owner);

        void fillFeed(final NewsFeedContract.Presenter usersPresenter, int start_from, FeedTypeEnum newsFeedTypeEnum);
    }
}
