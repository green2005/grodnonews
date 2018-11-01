package com.green.grodnonews.ui;

import android.os.Bundle;

import com.green.grodnonews.FeedTypeEnum;

public class NewGrodnoFragment extends NewsFeedFragment {

    public static NewsFeedFragment getFragment(Bundle params) {
        NewsFeedFragment fragment = new NewGrodnoFragment();
        fragment.setArguments(params);
        return fragment;
    }

    @Override
    public FeedTypeEnum getFeedType() {
        return FeedTypeEnum.NEWGRODNO;
    }
}
