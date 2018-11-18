package com.green.grodnonews.parser;

import android.os.Build;
import android.text.Html;

import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.room.NewsDetailItem;

import java.util.List;

public abstract class DetailProcessor {

    public static List<NewsDetailItem> getDetailItems(String url, String response, FeedTypeEnum feedType) throws Exception {
        DetailProcessor processor = getProcessor(feedType);
        if (processor != null) {
            return processor.getNewsDetail(url, response);
        } else
            return null;
    }

    private static DetailProcessor getProcessor(FeedTypeEnum feedType) {
        DetailProcessor processor = null;
        switch (feedType) {
            case NEWGRODNO: {
                processor = new NewGrodnoDetailProcessor();
                break;
            }
            case S13: {
                processor = new S13DetailProcessor();
                break;
            }
            case HRODNALIFE: {
                processor = new HrodnaLifeDetailProcessor();
                break;
            }
        }
        return processor;
    }

    String getStringFromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }

    }

    public abstract List<NewsDetailItem> getNewsDetail(String url, String serverResponse) throws Exception;

}
