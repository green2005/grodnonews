package com.green.grodnonews.network;

import com.green.grodnonews.API;
import com.green.grodnonews.FeedTypeEnum;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BlogDataSource extends NetworkDataSource{
    private OkHttpClient mClient;

    public BlogDataSource() {
        mClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public InputStream getDetailData(String url)throws Exception{
        return getResponse(url);
    }

    public InputStream getFeedData(final int start_from, final FeedTypeEnum newsFeedTypeEnum) throws Exception {
        String url = API.getFeedUrl(start_from  , newsFeedTypeEnum);
        return getResponse(url);
    }

    private InputStream getResponse(String url) throws Exception{
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = mClient.newCall(request).execute();
        } catch (Exception e) {
            throw e;
        }

        if (!response.isSuccessful()) {
            throw new IOException("Incorrect response" + response.code());
        }

        return response.body().byteStream();
    }

}
