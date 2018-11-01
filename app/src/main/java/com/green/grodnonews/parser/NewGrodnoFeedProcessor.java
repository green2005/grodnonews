package com.green.grodnonews.parser;

import android.text.TextUtils;

import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.room.NewsFeedItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NewGrodnoFeedProcessor extends FeedProcessor {
    @Override
    public List<NewsFeedItem> parseFeed(InputStream response, FeedTypeEnum feedType, int offset) throws Exception {
        List<NewsFeedItem> feed = new ArrayList<>();
        JSONArray ja = new JSONObject(getStringFromStream(response,"")).optJSONObject("response").optJSONArray("items");
        for (int i = 0; i < ja.length(); i++) {
            JSONObject post = ja.optJSONObject(i);
            if (post.optInt("is_pinned") == 1) continue;
            if (post.optInt("marked_as_ads") == 1) continue;
            // String s13Name = mContext.getResources().getString(R.string.s13_name);
            NewsFeedItem feedItem = getFeedItem(post);
            if (!TextUtils.isEmpty(feedItem.id)) {
                feedItem.feedSourceId = FeedTypeEnum.NEWGRODNO.getAsInt();
                feedItem.offset = offset;
                feed.add(feedItem);
            }
        }
        return feed;
    }

    private NewsFeedItem getFeedItem(JSONObject post) {
        NewsFeedItem feedItem = new NewsFeedItem();
        feedItem.url = "";
        JSONArray attachments = post.optJSONArray("attachments");
        if (attachments != null) {
            for (int i = 0; i < attachments.length(); i++) {
                JSONObject attachment = attachments.optJSONObject(i);
                JSONObject link = attachment.optJSONObject("link");
                if (link != null) {
                    feedItem.date = post.optString("date");
                    feedItem.id = post.optString("id");
                    feedItem.url = link.optString("url");
                    feedItem.title = link.optString("title");
                    if (TextUtils.isEmpty(feedItem.title)){
                        feedItem.title = post.optString("text");
                    }
                    feedItem.text = getShortDesription(link.optString("description"));
                    JSONObject photo = link.optJSONObject("photo");
                    if (photo != null) {
                        JSONArray photos = photo.optJSONArray("sizes");
                        for (int j = 0; j < photos.length(); j++) {
                            String type = photos.optJSONObject(j).optString("type");
                            if (("l".equalsIgnoreCase(type))) {
                                feedItem.imgUrl = photos.optJSONObject(j).optString("url");
                                feedItem.imageWidth = photos.optJSONObject(j).optInt("width");
                                feedItem.imageHeigt = photos.optJSONObject(j).optInt("height");
                                break;
                            }
                        }
                    }
                }

            }
        }
        feedItem.date = feedDateToDate(feedItem.date);
        return feedItem;
    }
}
