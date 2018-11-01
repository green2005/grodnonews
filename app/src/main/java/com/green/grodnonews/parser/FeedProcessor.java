package com.green.grodnonews.parser;

import android.text.TextUtils;

import com.google.api.client.util.IOUtils;
import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.room.NewsFeedItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class FeedProcessor {
    private static final String s_Eol = ". ";
    private Format mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static String getShortDesription(String longDescription) {
        int i = longDescription.indexOf(s_Eol);
        int i1 = longDescription.indexOf(s_Eol, i + 1);
        if (i1 <= 0) {
            i1 = i;
        }

        if (i1 > 0) {
            return longDescription.subSequence(0, i1) + "...";
        } else {
            return longDescription;
        }
    }

    String feedDateToDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return date;
        } else {
            Date d = new Date();
            d.setTime((long) Integer.parseInt(date) * 1000);
            return mFormat.format(d);
        }
    }


    public static List<NewsFeedItem> getFeedList(InputStream serverResponse, FeedTypeEnum feedType, int offset) throws Exception {
        FeedProcessor processor = null;
        switch (feedType) {
            case NEWGRODNO:
                processor = new NewGrodnoFeedProcessor();
                break;
            case S13:
                processor = new S13FeedProcessor();
                break;
            case HRODNALIFE:
                processor = new HrodnaLifeFeedProcessor();
                break;
        }
        return processor.parseFeed(serverResponse, feedType, offset);
    }

    public static String getStringFromStream(InputStream stream, String decodingCharset) throws Exception {
        InputStreamReader is;
        if (!TextUtils.isEmpty(decodingCharset)) {
            is = new InputStreamReader(stream, decodingCharset);
        } else {
            is = new InputStreamReader(stream);
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();
        while (read != null) {
            //System.out.println(read);
            sb.append(read);
            read = br.readLine();
        }
        return sb.toString();
    }


    public abstract List<NewsFeedItem> parseFeed(InputStream response, FeedTypeEnum feedType, int offset) throws Exception;
}
