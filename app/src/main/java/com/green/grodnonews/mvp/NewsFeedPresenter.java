package com.green.grodnonews.mvp;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.SparseIntArray;
import com.green.grodnonews.App;
import com.green.grodnonews.FeedTypeEnum;


public class NewsFeedPresenter extends ViewModel implements NewsFeedContract.Presenter {
    private App mApp = null;
    private NewsFeedContract.View mView;
    private SparseIntArray mFeedOffsets;

    @Override
    public void onViewRequestFeedStart(NewsFeedContract.View contractView, Context context) {
        mFeedOffsets.put(contractView.getFeedType().getAsInt(), 0);
        fillFeed(contractView);
    }

    @Override
    public void onViewRequestFeedNext(NewsFeedContract.View contractView, Context context) {
        int offset = mFeedOffsets.get(contractView.getFeedType().getAsInt());
        mFeedOffsets.put(contractView.getFeedType().getAsInt(), offset + 1);
        fillFeed(contractView);
    }

    private void fillFeed(NewsFeedContract.View contractView) {
        int offset = mFeedOffsets.get(contractView.getFeedType().getAsInt());
        mApp.getRepository().fillFeed(this, offset, contractView.getFeedType());
    }

    @Override
    public void onViewCreated(NewsFeedContract.View contractView, Context context) {
        mApp = (App) context.getApplicationContext();
        mApp.getRepository().addObserver(contractView.getFeedType(), contractView.getLifecycleOwner(), contractView.getObserver());
        mView = contractView;
        mFeedOffsets = new SparseIntArray();
        for (FeedTypeEnum a : FeedTypeEnum.values()) {
            mFeedOffsets.put(a.getAsInt(), 0);
        }
        fillFeed(contractView);
    }

    @Override
    public void onViewDestroyed(NewsFeedContract.View contractView) {
        mApp.getRepository().removerObserver(contractView.getFeedType(), contractView.getLifecycleOwner());
        mView = null;
    }

    @Override
    public void showError(String error) {
        mView.showError(error);
    }
}
