package com.green.grodnonews.ui;

import android.os.Bundle;

import com.green.grodnonews.FeedTypeEnum;

public class S13FeedFragment extends NewsFeedFragment {

    public static NewsFeedFragment getFragment(Bundle params) {
        NewsFeedFragment fragment = new S13FeedFragment();
        fragment.setArguments(params);
        return fragment;
    }

    @Override
    public FeedTypeEnum getFeedType() {
        return FeedTypeEnum.S13;
    }
}
