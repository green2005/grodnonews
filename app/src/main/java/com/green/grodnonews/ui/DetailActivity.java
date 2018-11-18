package com.green.grodnonews.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.R;
import com.green.grodnonews.ThemeHelper;
import com.green.grodnonews.loader.ImageLoader;
import com.green.grodnonews.mvp.ThemePresenter;
import com.green.grodnonews.room.NewsFeedItem;

public class DetailActivity extends AppCompatActivity {
    public static final String FEED_ITEM_KEY = "rv_feed_item";
    private NewsFeedItem mFeedItem;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemePresenter themePresenter = ViewModelProviders.of(this).get(ThemePresenter.class);
        themePresenter.onViewCreated(this);
        setTheme(themePresenter.getTheme());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout la = findViewById(R.id.main_collapsing);

        int color = ThemeHelper.getColor(this, R.color.color_toolbar_title_collapsed);
        la.setCollapsedTitleTextColor(color);

        color = ThemeHelper.getColor(this, R.color.color_toolbar_title_expanded);
        la.setExpandedTitleColor(color);


        if ((getIntent().getExtras() != null) && (getIntent().getExtras().containsKey(FEED_ITEM_KEY))) {
            mFeedItem = getIntent().getExtras().getParcelable(FEED_ITEM_KEY);
            FeedTypeEnum e = FeedTypeEnum.getTypeById(mFeedItem.feedSourceId);
            if (e != null) {
                la.setTitle(e.getTitle());
            }
            ResizableImageView imageView = findViewById(R.id.image);
            if (TextUtils.isEmpty(mFeedItem.imgUrl)) {
                imageView.setVisibility(View.GONE);
                final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams)
                        la.getLayoutParams();
                params.setScrollFlags(0);
                la.setLayoutParams(params);

                toolbar.setVisibility(View.VISIBLE);

                AppBarLayout a = findViewById(R.id.main_appbar);
                a.setExpanded(true);

            } else {
                ImageLoader.get(this).loadImage(imageView, mFeedItem.imgUrl);
                imageView.setOriginalImageSize(mFeedItem.imageWidth, mFeedItem.imageHeigt);
            }

            Bundle b = new Bundle();
            b.putParcelable(FEED_ITEM_KEY, mFeedItem);
            Fragment detailFragment = DetailFragment.getDetailFragment(b);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_fragment, detailFragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_detail, menu);
        return true;
    }

    private void shareLink() {
        Intent iShare = new Intent(Intent.ACTION_SEND);
        iShare.setType("text/plain");
        iShare.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        iShare.putExtra(Intent.EXTRA_SUBJECT, mFeedItem.title);
        iShare.putExtra(Intent.EXTRA_TEXT, mFeedItem.url);
        FeedTypeEnum e = FeedTypeEnum.getTypeById(mFeedItem.feedSourceId);
        String sTitle = "share";
        if (e != null) {
            sTitle = e.getTitle();
        }
        startActivity(Intent.createChooser(iShare, sTitle));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.share: {
                shareLink();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
