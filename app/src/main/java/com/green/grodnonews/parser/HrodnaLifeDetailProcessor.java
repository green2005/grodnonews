package com.green.grodnonews.parser;

import android.text.TextUtils;

import com.green.grodnonews.DetailTypeEnum;
import com.green.grodnonews.room.NewsDetailItem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HrodnaLifeDetailProcessor extends DetailProcessor {
    @Override
    public List<NewsDetailItem> getNewsDetail(String url, String serverResponse) throws Exception {
        if (TextUtils.isEmpty(url)) return null;

        if (url.startsWith("https://m.vk.com")) {
            return getResponseVk(url, serverResponse);
        } else {
            return getResponse(url, serverResponse);
        }
    }

    private static final String FIGURE_PREF = "<imgclass=";
    private static final String FIGURE_POSTF = "/>";

    private static final String IMAGE_PREF = "src=\"";
    private static final String IMAGE_POSTF = "\"";

    private List<NewsDetailItem> getResponse(String url, String serverResponse) throws Exception {
        //property="og:description"
        List<NewsDetailItem> items = new ArrayList<>();
        Pattern pTitle = Pattern.compile("<title>.*<\\/title>");
        Pattern pstyle = Pattern.compile("<style>(.*?)</style>");
        Matcher mStyle;
        String textPrefix = "<divclass=\"post-content";// "class=\"fa fa-arrows-alt\">";
        String textPostfix = "<script"; // "class='youtube-player'";

        Pattern pText = Pattern.compile(textPrefix + "(.*?|.*/S)" + textPostfix);
        Pattern pFigure = Pattern.compile(FIGURE_PREF + "(.*?)" + FIGURE_POSTF);
        Pattern pImage = Pattern.compile(IMAGE_PREF + "(.*?)" + IMAGE_POSTF);
        //.matcher(serverResponse.replace("\n","")).find()

        Matcher m = pTitle.matcher(serverResponse);
        if (m.find()) {
            NewsDetailItem title = new NewsDetailItem();
            title.itemType = DetailTypeEnum.TITLE.getAsInt();
            title.articleUrl = url;
            title.content = getStringFromHtml(m.group());
            items.add(title);
        }
        m = pText.matcher(serverResponse.replace("\n", ""));
        if (m.find()) {
            String content = m.group().substring(textPrefix.length());
            int k = content.indexOf("<");
            if (k > 0) {
                content = content.substring(k);
            }

            m = pFigure.matcher(content);
            int textStart = 0;
            int textEnd;
            while (m.find()) {
                String figure = m.group();
                textEnd = m.start();
                NewsDetailItem text = new NewsDetailItem();
                text.itemType = DetailTypeEnum.SPLASH_TEXT.getAsInt();
                text.articleUrl = url;
                text.content = content.substring(textStart, textEnd);
                mStyle = pstyle.matcher(text.content);
                text.content = mStyle.replaceAll("");
                items.add(text);

                Matcher mImage = pImage.matcher(figure);
                if (mImage.find()) {
                    String imageUrl = mImage.group().substring(IMAGE_PREF.length());
                    imageUrl = imageUrl.substring(0, imageUrl.length() - IMAGE_POSTF.length());
                    NewsDetailItem img = new NewsDetailItem();
                    img.articleUrl = url;
                    img.itemType = DetailTypeEnum.IMAGE.getAsInt();
                    img.content = imageUrl;
                    img.itemUrl = imageUrl;
                    items.add(img);
                }
                textStart = m.end();
            }

            textEnd = content.length();
            NewsDetailItem text = new NewsDetailItem();
            text.itemType = DetailTypeEnum.SPLASH_TEXT.getAsInt();
            text.articleUrl = url;
            text.content = content.substring(textStart, textEnd);

            mStyle = pstyle.matcher(text.content);
            text.content = mStyle.replaceAll("");

            if (!TextUtils.isEmpty(text.content)) {
                items.add(text);
            }
        }
        return items;
    }

    private List<NewsDetailItem> getResponseVk(String url, String serverResponse) throws Exception {
        List<NewsDetailItem> items = new ArrayList<>();
        Pattern pTitle = Pattern.compile("<title>.*<\\/title>");
        Pattern pText = Pattern.compile("<p  class=\"article_decoration_first\"(.*?)<div class=\"article_bottom_extra_", Pattern.MULTILINE);
        //.matcher(serverResponse.replace("\n","")).find()

        Matcher m = pTitle.matcher(serverResponse);
        if (m.find()) {
            NewsDetailItem title = new NewsDetailItem();
            title.itemType = DetailTypeEnum.TITLE.getAsInt();
            title.articleUrl = url;
            title.content = getStringFromHtml(m.group());
            items.add(title);
        }
        m = pText.matcher(serverResponse.replace("\n", ""));
        if (m.find()) {
            NewsDetailItem text = new NewsDetailItem();
            text.itemType = DetailTypeEnum.SPLASH_TEXT.getAsInt();
            text.articleUrl = url;
            text.content = getStringFromHtml(m.group());
            items.add(text);
        }
        return items;
    }

}
