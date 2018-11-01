package com.green.grodnonews.adapters;


import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.green.grodnonews.API;
import com.green.grodnonews.DetailTypeEnum;
import com.green.grodnonews.mvp.ThemePresenter;
import com.green.grodnonews.ui.ImageClickListener;
import com.green.grodnonews.R;
import com.green.grodnonews.ui.ResizableImageView;
import com.green.grodnonews.loader.ImageLoader;
import com.green.grodnonews.room.NewsDetailItem;


import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailAdapter extends RecyclerView.Adapter {
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private List<NewsDetailItem> mItems;
    private Context mContext;
    private ThemePresenter mThemePresenter;
    private ThumbnailListener mThumbnailListener;
    private int mTitleFontSize;
    private int mContentFontSize;
    private String mTitleImage;
    private FloatingActionButton mFab = null;

    private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> mThumbnailViewToLoaderMap = new HashMap<>();


    private class TitleHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        TitleHolder(View itemView) {
            super(itemView);
        }
    }

    private class ImageHolder extends RecyclerView.ViewHolder {
        ResizableImageView imageView;

        ImageHolder(View itemView) {
            super(itemView);
        }
    }

    private class VideoHolder extends RecyclerView.ViewHolder {
        YouTubeThumbnailView thumbnailView;
        ImageView imageView;

        VideoHolder(View itemView) {
            super(itemView);
        }
    }

    private class SplashTextHolder extends RecyclerView.ViewHolder {
        TextView tvText;

        SplashTextHolder(View itemView) {
            super(itemView);
        }
    }
    //TITLE(0), IMAGE(1), VIDEO(2), SPLASH_TEXT(3), COMMENT(4), COMMENT_TITLE(5);

    private class CommentHolder extends RecyclerView.ViewHolder {
        TextView tvComment;
        TextView tvAuthor;

        CommentHolder(View itemView) {
            super(itemView);
        }
    }

    private class CommentTitleHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        CommentTitleHolder(View itemView) {
            super(itemView);
        }
    }

    public DetailAdapter(Context context, AppCompatActivity activity) {
        mInflater = LayoutInflater.from(context);
        mImageLoader = ImageLoader.get(context);
        mContext = context;
        mThumbnailListener = new ThumbnailListener(context);
        mThemePresenter = ViewModelProviders.of(activity).get(ThemePresenter.class);
        mThemePresenter.onViewCreated(activity);
        mThemePresenter.getTitleFontSize().observe(activity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                mTitleFontSize = value;
                DetailAdapter.this.notifyDataSetChanged();
            }
        });
        mThemePresenter.getContentFontSize().observe(activity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                mContentFontSize = value;
                DetailAdapter.this.notifyDataSetChanged();
            }
        });
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DetailTypeEnum detailType = DetailTypeEnum.getTypeById(viewType);
        switch (detailType) {
            case IMAGE: {
                View v = mInflater.inflate(R.layout.rv_newsdetail_item_image, parent, false);
                ImageHolder h = new ImageHolder(v);
                h.imageView = v.findViewById(R.id.photo);
                h.imageView.setOnClickListener(new ImageClickListener(h.imageView, mContext));
                return h;
            }
            case COMMENT: {
                View v = mInflater.inflate(R.layout.rv_newsdetail_item_comment, parent, false);
                CommentHolder h = new CommentHolder(v);
                h.tvComment = v.findViewById(R.id.tvComment);
                h.tvAuthor = v.findViewById(R.id.tvAuthor);
                return h;
            }
            case COMMENT_TITLE: {
                View v = mInflater.inflate(R.layout.rv_newsdetail_item_comment_title, parent, false);
                CommentTitleHolder h = new CommentTitleHolder(v);
                h.tvTitle = v.findViewById(R.id.tvTitle);
                return h;
            }
            case SPLASH_TEXT: {
                View v = mInflater.inflate(R.layout.rv_newsdetail_item_splash_text, parent, false);
                SplashTextHolder h = new SplashTextHolder(v);
                h.tvText = v.findViewById(R.id.tvText);
                return h;
            }

            case TITLE: {
                View v = mInflater.inflate(R.layout.rv_newsdetail_item_title, parent, false);
                TitleHolder h = new TitleHolder(v);
                h.tvTitle = v.findViewById(R.id.tvTitle);
                return h;
            }
            case VIDEO: {
                View v = mInflater.inflate(R.layout.rv_newsdetail_item_video, parent, false);
                VideoHolder h = new VideoHolder(v);
                h.thumbnailView = v.findViewById(R.id.thumbnail);
                h.imageView = v.findViewById(R.id.playImage);
                return h;
            }
        }
        return null;
    }

    public void setTitleImage(String url) {
        mTitleImage = url;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).itemType;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DetailTypeEnum itemType = DetailTypeEnum.getTypeById(getItemViewType(position));
        switch (itemType) {
            case TITLE: {
                ((TitleHolder) (holder)).tvTitle.setText(getStringFromHtml(mItems.get(position).content));
                ((TitleHolder) (holder)).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleFontSize);
                break;
            }
            case SPLASH_TEXT: {
                ((SplashTextHolder) (holder)).tvText.setText(getStringFromHtml(mItems.get(position).content));
                ((SplashTextHolder) (holder)).tvText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContentFontSize);
                break;
            }
            case IMAGE: {
                NewsDetailItem item = mItems.get(position);
                int w = 0;
                int h = 0;
                if (!TextUtils.isEmpty(item.width)) {
                    w = Integer.parseInt(item.width);
                    h = Integer.parseInt(item.height);
                }
                String url = item.itemUrl;
                setImage(w, h, ((ImageHolder) (holder)).imageView, url);
                break;
            }
            case VIDEO: {
                NewsDetailItem item = mItems.get(position);
                String url = item.itemUrl;
                setVideo(((VideoHolder) (holder)), url);
                // ((VideoHolder) (holder)).tvText.setText(item.itemUrl);
                break;
            }
            case COMMENT: {
                NewsDetailItem item = mItems.get(position);
                ((CommentHolder) (holder)).tvComment.setText(getStringFromHtml(item.content));
                ((CommentHolder) (holder)).tvAuthor.setText(item.authorName);
                ((CommentHolder) (holder)).tvComment.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContentFontSize);
                ((CommentHolder) (holder)).tvAuthor.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContentFontSize);
                break;
            }
            case COMMENT_TITLE: {
                ((CommentTitleHolder) (holder)).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleFontSize);
            }
        }
    }

    private void setVideo(VideoHolder holder, String url) {
        YouTubeThumbnailView thumbnail = holder.thumbnailView;
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youTubeId = (String) v.getTag();
                Intent intent = YouTubeIntents.createPlayVideoIntentWithOptions(mContext, youTubeId, true, false);
                mContext.startActivity(intent);
            }
        });

        thumbnail.setTag(url);
        thumbnail.initialize(API.YOUTUBE_KEY, mThumbnailListener);
    }

    private void setImage(int width, int height, ResizableImageView imageView, String url) {
        if ((TextUtils.isEmpty(url)) || (url.equalsIgnoreCase(mTitleImage))) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            if (width > 0) {
                imageView.setOriginalImageSize(width, height);
            }
            if (mImageLoader != null) {
                mImageLoader.loadImage(imageView, url);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mItems == null) {
            return 0;
        } else {
            return mItems.size();
        }
    }

    String getStringFromHtml(String html) {
        if (TextUtils.isEmpty(html)) {
            return "";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }

    }

    public void setItems(List<NewsDetailItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }


    private final class ThumbnailListener implements
            YouTubeThumbnailView.OnInitializedListener,
            YouTubeThumbnailLoader.OnThumbnailLoadedListener {


        ThumbnailListener(Context context) {
            mContext = context;
        }

        @Override
        public void onInitializationSuccess(
                YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
            loader.setOnThumbnailLoadedListener(this);
            mThumbnailViewToLoaderMap.put(view, loader);
            String videoId = (String) view.getTag();
            loader.setVideo(videoId);
        }

        @Override
        public void onInitializationFailure(
                YouTubeThumbnailView view, YouTubeInitializationResult loader) {
            //Toast.makeText(mContext, "initFail", Toast.LENGTH_SHORT).show();
            // loader.getErrorDialog(this.mContext, 0);
            view.setVisibility(View.GONE);
            //initialize_failed = true;
        }

        @Override
        public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
            //Toast.makeText(mContext, "loaded",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onThumbnailError(YouTubeThumbnailView view, YouTubeThumbnailLoader.ErrorReason errorReason) {
            //   view.setImageResource(R.drawable.no_thumbnail);
            //Toast.makeText(mContext, errorReason.toString(),Toast.LENGTH_SHORT).show();

        }
    }

}
