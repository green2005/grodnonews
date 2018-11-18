package com.green.grodnonews.ui;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.R;
import com.green.grodnonews.adapters.DetailAdapter;
import com.green.grodnonews.mvp.NewsDetailContract;
import com.green.grodnonews.mvp.NewsDetailPresenter;
import com.green.grodnonews.room.NewsDetailItem;
import com.green.grodnonews.room.NewsFeedItem;

import java.util.List;

public class DetailFragment extends Fragment implements NewsDetailContract.View, Observer<List<NewsDetailItem>> {
    private static final String REPLY_PREFIX = "<b>@ %s</b>:\n";

    private NewsDetailPresenter mPresenter;
    private NewsFeedItem mNewsFeedItem;
    private DetailAdapter mAdapter;
    private SwipeRefreshLayout mSwipe;
    private ProgressBar mProgress;
    private boolean mIsLoading = false;
    private FloatingActionButton mEdit;
    private NewsDetailItem mDetail;
    private boolean mCommentAdded = false;
    private RecyclerView mRecyclerView;
    private Snackbar mSnackBar = null;
    private Boolean mHideUser = false;

    private static final int SNACKBAR_TIMEOUT = 3000;

    public static Fragment getDetailFragment(Bundle params) {
        Fragment fragment = new DetailFragment();
        fragment.setArguments(params);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = ViewModelProviders.of(this).get(NewsDetailPresenter.class);
        Bundle b = getArguments();

        if ((b != null) && (b.containsKey(DetailActivity.FEED_ITEM_KEY))) {
            mNewsFeedItem = b.getParcelable(DetailActivity.FEED_ITEM_KEY);
            mPresenter.onCreateView(getActivity(), this, mNewsFeedItem.url);
            mIsLoading = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        mProgress = view.findViewById(R.id.progress);
        mEdit = view.findViewById(R.id.btn_edit);
        mPresenter.onRequestData(mNewsFeedItem.url, FeedTypeEnum.getTypeById(mNewsFeedItem.feedSourceId));
        mProgress.setVisibility(View.VISIBLE);
        mProgress.setActivated(true);

        mSwipe = view.findViewById(R.id.swipe);
        mSwipe.setRefreshing(true);
        mSwipe.setActivated(true);
        mRecyclerView = view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new DetailAdapter(getActivity(), (AppCompatActivity) getActivity());
        mAdapter.setOnUserMenuMoreClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        mAdapter.setTitleImage(mNewsFeedItem.imgUrl);
        mRecyclerView.setAdapter(mAdapter);

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        if (supportsEditing()) {
            mEdit.setVisibility(View.VISIBLE);
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0 && mEdit.getVisibility() == View.VISIBLE) {
                        mEdit.hide();
                    } else if (dy < 0 && mEdit.getVisibility() != View.VISIBLE) {
                        mEdit.show();
                    }
                }
            });
            mEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.onAddCommentClick(DetailFragment.this.getActivity(), getFragmentManager(), mDetail, "");
                }
            });
        } else {
            mEdit.setVisibility(View.GONE);
        }
        return view;
    }


    private void showPopupMenu(final View v) {
        final NewsDetailItem newsDetailItem = mAdapter.getItemAtPos((Integer) v.getTag());
        if (newsDetailItem == null) return;

        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        popupMenu.inflate(R.menu.menu_user_popup);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String author = newsDetailItem.authorName;
                switch (item.getItemId()) {
                    case (R.id.menu_answer): {
                        String reply = String.format(REPLY_PREFIX, author);
                        mPresenter.onAddCommentClick(DetailFragment.this.getActivity(), getFragmentManager(), mDetail, reply);
                        break;
                    }
                    case (R.id.menu_add_to_blacklist): {
                        hideUserComments(author);
                        break;
                    }
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void hideUserComments(final String userName) {
        View v = getView();
        mHideUser = true;
        if (v == null) return;
        mSnackBar = Snackbar.make(v, "Пользователь будет скрыт",
                Snackbar.LENGTH_LONG).setAction("Отменить?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mSnackBar != null) && (mSnackBar.isShown())) {
                    mSnackBar.dismiss();
                    mHideUser = false;
                }
            }
        });
        final Handler h = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SNACKBAR_TIMEOUT);
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mHideUser) {
                                mPresenter.addUserToBlackList(userName, mNewsFeedItem.url, FeedTypeEnum.getTypeById(mNewsFeedItem.feedSourceId));
                            }
                        }
                    });
                } catch (Exception e) {
                }
            }
        }).start();
        mSnackBar.show();
    }

    private void refreshData(){
        if (!mIsLoading) {
            mPresenter.onRequestData(mNewsFeedItem.url, FeedTypeEnum.getTypeById(mNewsFeedItem.feedSourceId));
            mIsLoading = true;
        }
    }


    @Override
    public void onDestroy() {
        mPresenter.onDestroyView(this);
        super.onDestroy();
    }

    @Override
    public String getUrl() {
        return mNewsFeedItem.url;
    }

    @Override
    public FeedTypeEnum getFeedType() {
        return FeedTypeEnum.getTypeById(mNewsFeedItem.feedSourceId);
    }

    @Override
    public LifecycleOwner getViewLifecycleOwner() {
        return this;
    }

    @Override
    public Observer<List<NewsDetailItem>> getObserver() {
        return this;
    }

    @Override
    public void processError(String errorText) {
        Toast.makeText(getActivity(), errorText, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onChanged(@Nullable List<NewsDetailItem> newsDetailItems) {
        if (((newsDetailItems != null) && (newsDetailItems.size() != 0)) || (mCommentAdded)) {
            mSwipe.setRefreshing(false);
            mProgress.setActivated(false);
            mProgress.setVisibility(View.GONE);
            mAdapter.setItems(newsDetailItems);
            mIsLoading = false;
            if (newsDetailItems.size() > 0) {
                mDetail = newsDetailItems.get(0);
            }

            if ((mCommentAdded) && (newsDetailItems != null)) {
                mRecyclerView.scrollToPosition(newsDetailItems.size() - 1);
            }
            mCommentAdded = false;
        }
    }

    private boolean supportsEditing() {
        if (getFeedType() == FeedTypeEnum.S13) {
            return true;
        } else
            return false;

    }

    @Override
    public void commentAdded() {
        mPresenter.onRequestData(mNewsFeedItem.url, FeedTypeEnum.getTypeById(mNewsFeedItem.feedSourceId));
        mIsLoading = true;
        mSwipe.setRefreshing(true);
        mSwipe.setActivated(true);
        mCommentAdded = true;
    }
}
