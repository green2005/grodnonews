package com.green.grodnonews.ui;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.green.grodnonews.ErrorHelper;
import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.mvp.NewsFeedContract;
import com.green.grodnonews.mvp.NewsFeedPresenter;
import com.green.grodnonews.R;
import com.green.grodnonews.adapters.FeedAdapter;
import com.green.grodnonews.mvp.ThemePresenter;
import com.green.grodnonews.room.NewsFeedItem;

import java.util.List;

public abstract class NewsFeedFragment extends Fragment implements NewsFeedContract.View, Observer<List<NewsFeedItem>> {
    public static final String FRAGMENT_NUMBER_KEY = "number";

    private NewsFeedPresenter mFeedPresenter;
    private FeedAdapter mAdapter;
    private SwipeRefreshLayout mSwipe;

    public static NewsFeedFragment getFragment(Bundle b) {
        int n = b.getInt(FRAGMENT_NUMBER_KEY);
        if (n == FeedTypeEnum.S13.getAsInt()) {
            return S13FeedFragment.getFragment(b);
        } else if (n == FeedTypeEnum.HRODNALIFE.getAsInt()) {
            return HroodnaLifeFragment.getFragment(b);
        } else if (n == FeedTypeEnum.NEWGRODNO.getAsInt()) {
            return NewGrodnoFragment.getFragment(b);
        } else return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFeedPresenter = ViewModelProviders.of(this).get(NewsFeedPresenter.class);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        setUpRecyclerView(v);
        mSwipe = v.findViewById(R.id.swipe);
        mFeedPresenter.onViewCreated(this, getActivity());
        // mFeedPresenter.onViewRequestFeedStart(this, getActivity());
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mFeedPresenter.onViewRequestFeedStart(NewsFeedFragment.this, getActivity());
            }
        });
        return v;
    }

    private void setUpRecyclerView(View v) {
        RecyclerView r = v.findViewById(R.id.recyclerview);
        r.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new FeedAdapter(getActivity(), this, new FeedAdapter.FeedAdapterListener() {
            @Override
            public void onDataRequest() {
                mFeedPresenter.onViewRequestFeedNext(NewsFeedFragment.this, NewsFeedFragment.this.getActivity());
            }

            @Override
            public void onItemClickListsener(int position) {
                //  Toast.makeText(NewsFeedFragment.this.getActivity(), mAdapter.getFeedItem(position).url, Toast.LENGTH_LONG).show();
                Bundle b = new Bundle();
                NewsFeedItem entity = mAdapter.getFeedItem(position);
                b.putParcelable(DetailActivity.FEED_ITEM_KEY, entity);
                Intent i = new Intent(NewsFeedFragment.this.getActivity(), DetailActivity.class);
                i.putExtras(b);
                startActivity(i);
            }
        });
        r.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        mFeedPresenter.onViewDestroyed(this);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void showError(String errorText) {
        ErrorHelper.processError(getActivity(), errorText);
    }

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return this;
    }

    @Override
    public Observer<List<NewsFeedItem>> getObserver() {
        return this;
    }


    @Override
    public void onChanged(@Nullable List<NewsFeedItem> newsFeedEntities) {
        mSwipe.setRefreshing(false);
        mAdapter.setFeed(newsFeedEntities);
    }
}
