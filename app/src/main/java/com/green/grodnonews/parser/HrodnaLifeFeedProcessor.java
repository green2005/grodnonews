package com.green.grodnonews.parser;

import android.text.TextUtils;

import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.room.NewsFeedItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HrodnaLifeFeedProcessor extends FeedProcessor {
    private Pattern pTitleUrl = Pattern.compile("https://(.*?)");

    @Override
    public List<NewsFeedItem> parseFeed(InputStream response, FeedTypeEnum feedType, int offset) throws Exception {
        List<NewsFeedItem> feed = new ArrayList<>();
        JSONArray ja = new JSONObject(getStringFromStream(response, "")).optJSONObject("response").optJSONArray("items");
        for (int i = 0; i < ja.length(); i++) {
            JSONObject post = ja.optJSONObject(i);
            //{"id":208324,"from_id":-51743326,"owner_id":-51743326,"date":1522931917,"marked_as_ads":0,"post_type":"post","text":"Теперь у всех появилась возможность в эфир добавить приветы, заявления, уведомления и какую-то ценную информацию. Каждый час на радио s13.ru выходит рубрика, посвященная вашим приветам. Как это работает и как добавить свой привет, читайте дальше","is_pinned":1,"attachments":[{"type":"link","link":{"url":"http:\/\/s13.ru\/archives\/206787","title":"Приветы, поздравления, информация: новая рубрика для слушателей на бот-радио s13.ru","caption":"s13.ru","description":"Чуть меньше двух месяцев назад мы запустили первое бот-радио Беларуси — радио s13.ru. Начинали скромно и без особого функционала — новости да музыка. Позже эфир пополнили джинглы, прогноз погоды, курсы обычных и криптовалют. А потом мы уволили первых радиоведущих — ботов Максима и Татьяну. Знаете, это также показало и то, что гродненцы не до конца читают новости, т.к. в социальных сетях и в коммен","photo":{"id":456249145,"album_id":-2,"owner_id":100,"sizes":[{"type":"m","url":"https:\/\/pp.userapi.com\/c845122\/v845122598\/1fbbb\/C3eh7RzLoLM.jpg","width":130,"height":58},{"type":"o","url":"https:\/\/pp.userapi.com\/c845122\/v845122598\/1fbbd\/OOIqqafF_u8.jpg","width":130,"height":87},{"type":"p","url":"https:\/\/pp.userapi.com\/c845122\/v845122598\/1fbbe\/o6oWAh7qBZg.jpg","width":200,"height":133},{"type":"q","url":"https:\/\/pp.userapi.com\/c845122\/v845122598\/1fbbf\/ejveuw9e-CY.jpg","width":320,"height":213},{"type":"r","url":"https:\/\/pp.userapi.com\/c845122\/v845122598\/1fbc0\/Cov-KG9RFPo.jpg","width":510,"height":240},{"type":"s","url":"https:\/\/pp.userapi.com\/c845122\/v845122598\/1fbba\/-aLJOq5SfF4.jpg","width":75,"height":33},{"type":"x","url":"https:\/\/pp.userapi.com\/c845122\/v845122598\/1fbbc\/HnwPZHx1lhA.jpg","width":537,"height":240}],"text":"","date":1522931916}}}],"post_source":{"type":"vk"},"comments":{"count":19,"groups_can_post":true,"can_post":1},"likes":{"count":82,"user_likes":0,"can_like":1,"can_publish":1},"reposts":{"count":2,"user_reposted":0},"views":{"count":697032}}
            if (post.optInt("is_pinned") == 1) continue;
            if (post.optInt("marked_as_ads") == 1) continue;
            // String s13Name = mContext.getResources().getString(R.string.s13_name);
            NewsFeedItem feedItem = getFeedItem(post);
            if ((feedItem != null) && (!TextUtils.isEmpty(feedItem.id)) && (!TextUtils.isEmpty(feedItem.url))) {
                feedItem.feedSourceId = FeedTypeEnum.HRODNALIFE.getAsInt();
                feedItem.offset = offset;
                feed.add(feedItem);
            }
        }
        return feed;
    }

    private NewsFeedItem getFeedItem(JSONObject post) {
        NewsFeedItem feedItem = new NewsFeedItem();
        feedItem.url = "";
        feedItem.date = post.optString("date");
        feedItem.title = post.optString("text");
        feedItem.id = post.optString("id");
        JSONArray attachments = post.optJSONArray("attachments");
        if (attachments == null) return feedItem;

        for (int j = 0; j < attachments.length(); j++) {
            JSONObject jo = attachments.optJSONObject(j);
            JSONObject link = jo.optJSONObject("link");
            JSONObject photo = null;
            if (link != null) {
                feedItem.url = link.optString("url");
                if (!TextUtils.isEmpty(link.optString("title"))) {
                    feedItem.title = link.optString("title");
                }
                photo = link.optJSONObject("photo");
            }
            if (photo == null) {
                photo = jo.optJSONObject("photo");
            }
            if (photo != null) {
                JSONArray photos = photo.optJSONArray("sizes");
                for (int i = 0; i < photos.length() - 1; i++) {
                    JSONObject ph = photos.optJSONObject(i);
                    String type = ph.optString("type");
                    if ("l".equalsIgnoreCase(type)||("z".equalsIgnoreCase(type))) {
                        feedItem.imgUrl = ph.optString("url");
                        feedItem.imageWidth = ph.optInt("width");
                        feedItem.imageHeigt = ph.optInt("height");
                        break;
                    }
                }
                if ((TextUtils.isEmpty(feedItem.imgUrl))&&(photos!=null)&&(photos.length()>0)){
                    JSONObject ph = photos.optJSONObject(photos.length() - 1);
                    feedItem.imgUrl = ph.optString("url");
                    feedItem.imageWidth = ph.optInt("width");
                    feedItem.imageHeigt = ph.optInt("height");
                }
            }
        }
        feedItem.date = feedDateToDate(feedItem.date);
        if ((TextUtils.isEmpty(feedItem.url)) && (!TextUtils.isEmpty(feedItem.title))) {
            feedItem.url = getUrlFromTitle(feedItem.title);
            feedItem.title = feedItem.title.replace(feedItem.url, "");
        }

        return feedItem;
    }

    String getUrlFromTitle(String title) {
        Matcher m = pTitleUrl.matcher(title);
        if (m.find()) {
            return title.substring(m.start());
        } else
            return "";
    }
}
