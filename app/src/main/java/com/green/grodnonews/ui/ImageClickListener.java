package com.green.grodnonews.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.green.grodnonews.loader.ImageLoader;
import com.green.grodnonews.ui.ImageZoomActivity;

public class ImageClickListener implements View.OnClickListener {
    private Context mContext;
    private ImageView mImageView;
    private ImageLoader mImageLoader;

    public ImageClickListener(ImageView imageView, Context context) {
        mImageView = imageView;
        mContext = context;
        mImageLoader = ImageLoader.get(context);
    }

    @Override
    public void onClick(View v) {
        String url = mImageLoader.getUrl(mImageView);
        Intent intent = new Intent(mContext, ImageZoomActivity.class);
        Bundle args = new Bundle();
        args.putString(ImageZoomActivity.IMAGE_URL, url);
        intent.putExtras(args);
        mContext.startActivity(intent);
    }
}
