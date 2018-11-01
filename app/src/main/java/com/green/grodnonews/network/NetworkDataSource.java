package com.green.grodnonews.network;

import android.content.Context;

import com.green.grodnonews.FeedTypeEnum;

import java.io.InputStream;

public abstract class NetworkDataSource {
    private static BlogDataSource sBlogDataSource;
    private static S13DataSource s13DataSource;

    public static NetworkDataSource getNetworkDataSource(Context context, FeedTypeEnum feedType) {
        if (feedType == FeedTypeEnum.S13) {
            if (s13DataSource == null) {
                s13DataSource = new S13DataSource(context);
            }
            return s13DataSource;
        } else {
            if (sBlogDataSource == null) {
                sBlogDataSource = new BlogDataSource();
            }
            return sBlogDataSource;
        }
    }

    public abstract InputStream getDetailData(String url) throws Exception;

    public abstract InputStream getFeedData(final int start_from, final FeedTypeEnum newsFeedTypeEnum) throws Exception;
}
