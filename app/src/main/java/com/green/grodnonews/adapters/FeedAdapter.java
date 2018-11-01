package com.green.grodnonews.adapters;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.green.grodnonews.R;
import com.green.grodnonews.mvp.ThemePresenter;
import com.green.grodnonews.ui.ResizableImageView;
import com.green.grodnonews.loader.ImageLoader;
import com.green.grodnonews.room.NewsFeedItem;

import java.util.List;

public class FeedAdapter<VH> extends RecyclerView.Adapter {

    public interface FeedAdapterListener {
        void onDataRequest();

        void onItemClickListsener(int position);
    }

    private enum DATA_STATE {
        BROWSING,
        LOADING
    }


    private class VH extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvContent;
        ResizableImageView photo;
        TextView tvDate;

        public VH(View itemView) {
            super(itemView);
        }
    }

    private LayoutInflater mInflater;
    private List<NewsFeedItem> mFeed;
    private DATA_STATE mState;
    private FeedAdapterListener mListener;
    private ImageLoader mImageLoader;
    private ThemePresenter mThemePresenter;
    private int mContentFontSize;
    private int mTitleFontSize;

    public FeedAdapter(Context context, Fragment fragment, FeedAdapterListener listener) {
        mInflater = LayoutInflater.from(context);
        mState = DATA_STATE.BROWSING;
        mListener = listener;
        mImageLoader = ImageLoader.get(context);
        mThemePresenter = ViewModelProviders.of(fragment.getActivity()).get(ThemePresenter.class);
        mThemePresenter.getContentFontSize().observe(fragment, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                mContentFontSize = value;
                FeedAdapter.this.notifyDataSetChanged();
            }
        });
        mThemePresenter.getTitleFontSize().observe(fragment, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                mTitleFontSize = value;
                FeedAdapter.this.notifyDataSetChanged();
            }
        });
        //  mContentFontSize = mThemePresenter.getContentFontSize().getValue();
        // mTitleFontSize = mThemePresenter.getTitleFontSize().getValue();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.rv_feed_item, parent, false);
        TextView tvTitle = v.findViewById(R.id.tvTitle);
        TextView tvContent = v.findViewById(R.id.tvContent);
        ResizableImageView imageView = v.findViewById(R.id.photo);


        FeedAdapter.VH h = new FeedAdapter.VH(v);
        h.tvTitle = tvTitle;
        h.photo = imageView;
        h.tvContent = tvContent;
        h.tvDate = v.findViewById(R.id.tvDate);

        return h;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        NewsFeedItem entity = mFeed.get(position);
        ((FeedAdapter.VH) (holder)).tvTitle.setText(entity.title);
        ((FeedAdapter.VH) (holder)).tvContent.setText(entity.text);
        ((FeedAdapter.VH) (holder)).tvDate.setText(entity.date);
        setImage(entity.imageWidth, entity.imageHeigt, ((FeedAdapter.VH) (holder)).photo, entity.imgUrl);
        if ((position == mFeed.size() - 1) && (mState == DATA_STATE.BROWSING)) {
            mState = DATA_STATE.LOADING;
            if (mListener != null) {
                mListener.onDataRequest();
            }
        }
        ((FeedAdapter.VH) (holder)).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClickListsener(position);
            }
        });

        ((FeedAdapter.VH) (holder)).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleFontSize);
        ((FeedAdapter.VH) (holder)).tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContentFontSize);
        ((FeedAdapter.VH) (holder)).tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContentFontSize);
    }

    private void setImage(int width, int height, ResizableImageView imageView, String url) {
        if (TextUtils.isEmpty(url)) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            imageView.setOriginalImageSize(width, height);
            if (mImageLoader != null) {
                mImageLoader.loadImage(imageView, url);
            }
        }
    }

    public NewsFeedItem getFeedItem(int position) {
        return mFeed.get(position);
    }

    public void setFeed(List<NewsFeedItem> feed) {
        mFeed = feed;
        notifyDataSetChanged();
        mState = DATA_STATE.BROWSING;
    }

    @Override
    public int getItemCount() {
        if (mFeed == null) {
            return 0;
        } else {
            return mFeed.size();
        }
    }
}
