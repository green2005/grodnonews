package com.green.grodnonews.parser;

import android.text.TextUtils;
import android.util.Xml;

import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.room.NewsFeedItem;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class S13FeedProcessor extends FeedProcessor {

    private String imgPref = "src=\"";
    private String imgPostF = "\"";
    private Pattern pImg = Pattern.compile(imgPref + "(.*?)" + imgPostF);

    private String imgPrefW = "width=\"";
    private Pattern pImgW = Pattern.compile(imgPrefW + "(.*?)" + imgPostF);

    private String imgPrefH = "height=\"";
    private Pattern pImgH = Pattern.compile(imgPrefH + "(.*?)" + imgPostF);

    private String textPref = "srcset=\"\" />";
    private String textPostf = "]";
    private Pattern pText = Pattern.compile(textPref + "(.*?)" + textPostf);

    @Override
    public List<NewsFeedItem> parseFeed(InputStream response, FeedTypeEnum feedType, int offset) throws Exception {
        boolean isItem = false;
        XmlPullParser xmlParser = Xml.newPullParser();
        xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        xmlParser.setInput(response, null);
        xmlParser.nextTag();
        List<NewsFeedItem> feed = new ArrayList<>();
        String title = null;
        String description = null;
        String link = null;
        String date = null;

        while (xmlParser.next() != XmlPullParser.END_DOCUMENT) {
            int eventType = xmlParser.getEventType();
            String name = xmlParser.getName();
            if (name == null)
                continue;

            if (eventType == XmlPullParser.END_TAG) {
                if (name.equalsIgnoreCase("item")) {
                    isItem = false;
                }
                continue;
            }

            if (eventType == XmlPullParser.START_TAG) {
                if (name.equalsIgnoreCase("item")) {
                    isItem = true;
                    continue;
                }
            }

            String result = "";
            if (xmlParser.next() == XmlPullParser.TEXT) {
                result = xmlParser.getText();
                xmlParser.nextTag();
            }

            if (name.equalsIgnoreCase("title")) {
                title = result;
                link = null;
                description = null;
                date = null;

            } else if (name.equalsIgnoreCase("link")) {
                link = result;
            } else if (name.equalsIgnoreCase("description")) {
                description = result;
            } else if (name.equalsIgnoreCase("pubDate")) {
                date = result;
            }

            if (title != null && link != null && description != null && date != null) {
                if (isItem) {
                    NewsFeedItem item = new NewsFeedItem();
                    item.title = title;
                    item.feedSourceId = FeedTypeEnum.S13.getAsInt();
                    fillImg(item, description);
                    item.url = link;
                    item.offset = offset;
                    item.date = date;
                    item.text = getTextDescription(description);
                    item.id = getItemId(link);

                    feed.add(item);
                }
                title = null;
                link = null;
                description = null;
                isItem = false;
                date = null;
            }
        }
        return feed;
    }

    private String getItemId(String link) throws Exception {
        URI uri = new URI(link);
        String[] segments = uri.getPath().split("/");
        return segments[segments.length - 1];
    }

    private void fillImg(NewsFeedItem item, String description) {
        Matcher m = pImg.matcher(description);
        if (m.find()) {
            String url = m.group().substring(imgPref.length());
            url = url.substring(0, url.length() - 1);
            url = url.replace("-196x60", "");
            item.imgUrl = url;
        }
    }

    private String getTextDescription(String description) {

        String s = description.replaceFirst("<img.*?/>", "");
        s = s.replaceFirst("<a href.*?/a>", "");
        if ((!TextUtils.isEmpty(s)) && (s.length() > 200)) {
            return s.substring(0, 200) + "...";
        } else {
            return s;
        }
    }

}
