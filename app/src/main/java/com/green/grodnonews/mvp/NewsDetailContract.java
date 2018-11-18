package com.green.grodnonews.mvp;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.room.NewsDetailItem;

import java.util.List;

public interface NewsDetailContract {
    interface View {
        String getUrl();

        FeedTypeEnum getFeedType();

        LifecycleOwner getViewLifecycleOwner();

        Observer<List<NewsDetailItem>> getObserver();

        void processError(String errorText);

        void commentAdded();

    }

    interface Presenter {
        void onCreateView(@NonNull Context context, @NonNull NewsDetailContract.View view, @NonNull String url);

        void onDestroyView(@NonNull NewsDetailContract.View view);

        void onRequestData(@NonNull String url, @NonNull FeedTypeEnum feedType);

        void processError(String errorText);

        void onAddCommentClick(Context context, FragmentManager fm, NewsDetailItem mDetail, String commentStartText);

        void addUserToBlackList(String userName, String dataUrl, FeedTypeEnum typeEnum);
    }

    interface Repository {
        void addObserver(String url, LifecycleOwner owner, Observer<List<NewsDetailItem>> observer);

        void removeObserver(String url, LifecycleOwner owner);

        void requestData(Presenter presenter, String url, FeedTypeEnum feedType);

        void addUserToBlackList(String userName, Presenter presenter, String url, FeedTypeEnum feedType);
    }

}
