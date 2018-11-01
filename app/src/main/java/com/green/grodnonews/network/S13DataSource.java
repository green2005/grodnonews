package com.green.grodnonews.network;

import android.content.Context;
import android.text.TextUtils;

import com.green.grodnonews.API;
import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.blogio.AccountSettings;
import com.green.grodnonews.blogio.RequestListener;
import com.green.grodnonews.blogio.S13Connector;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class S13DataSource extends NetworkDataSource {
    private AccountSettings mAccountSettings;
    private S13Connector mConnector;
    private static final String UTF8_CHARSET = "UTF-8";
    private OkHttpClient mClient;


    S13DataSource(Context context) {
        mConnector = S13Connector.getBlogConnector(); //new S13Connector();
        mAccountSettings = new AccountSettings(context);
        mClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void setAccountSettings(AccountSettings settings) {
        mAccountSettings = settings;
        login();
    }

    private void login() {
        if ((mAccountSettings != null) && (!TextUtils.isEmpty(mAccountSettings.getUserName())) && (!mConnector.loggedIn())) {
            mConnector.loginAsync(mAccountSettings.getUserName(), mAccountSettings.getPwd(), new RequestListener() {
                @Override
                public void onRequestDone(S13Connector.QUERY_RESULT result, String errorMessage) {

                }
            });
        }
    }

    @Override
    public InputStream getDetailData(String url) throws Exception {
        if ((mAccountSettings != null) && (!TextUtils.isEmpty(mAccountSettings.getUserName())) && (!mConnector.loggedIn())) {
            mConnector.doLogin(mAccountSettings.getUserName(), mAccountSettings.getPwd());
        }
        return mConnector.getInputStream(url, UTF8_CHARSET);
    }

    @Override
    public InputStream getFeedData(int start_from, FeedTypeEnum newsFeedTypeEnum) throws Exception {
        String url = API.getFeedUrl(start_from, newsFeedTypeEnum);
        return getResponse(url);
    }

    private InputStream getResponse(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response;
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

    public void addComment(final String commentText, final String akismet, final String ak_js, final String postId, final RequestListener listener) {
        mConnector.addComment(commentText, akismet, ak_js, postId, listener);

    }

}
