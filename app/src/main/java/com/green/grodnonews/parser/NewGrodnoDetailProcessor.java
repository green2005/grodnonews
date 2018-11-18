package com.green.grodnonews.parser;

import android.text.TextUtils;

import com.green.grodnonews.DetailTypeEnum;
import com.green.grodnonews.room.NewsDetailItem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewGrodnoDetailProcessor extends DetailProcessor {

    private static final String STextStart = "entry-content entry clearfix\">";
    private static final String STextEnd = "<div class=\"vortex";
    private static final String SImgStart1 = "<img";
    private static final String SImgStart2 = "src=\"";
    private static final String SImgEnd = "\"";
    private static final String STitleStart = "<title>";
    private static final String STitleEnd = "<\\/title>";
    private static final String SScript = "<script(.*?)<\\/script>";

    @Override
    public List<NewsDetailItem> getNewsDetail(final String url, String serverResponse) throws Exception {
        int textStart = 0, textEnd = 0;
        serverResponse = serverResponse.replace("\n", "");
        List<NewsDetailItem> items = new ArrayList<>();
        Pattern pTitle = Pattern.compile(STitleStart + "(.*?)" + STitleEnd);
        Pattern pText = Pattern.compile(STextStart + "(.*?)" + STextEnd);
        Pattern pImg1 = Pattern.compile(SImgStart1 + "(.*?)" + "/>");
        Pattern pImg2 = Pattern.compile(SImgStart2 + "(.*?)" + SImgEnd);

        Matcher m = pTitle.matcher(serverResponse);
        if (m.find()) {
            NewsDetailItem item = new NewsDetailItem();
            item.articleUrl = url;
            item.itemType = DetailTypeEnum.TITLE.getAsInt();
            item.content = m.group().substring(STitleStart.length());
            item.content = item.content.substring(0, item.content.length() - STitleEnd.length() + 1);
            items.add(item);
        }

        m = pText.matcher(serverResponse);
        if (!m.find()) {
            return null;
        }
        String text = m.group().substring(STextStart.length());
        text = text.replaceAll(SScript, "");

        m = pImg1.matcher(text);
        while (m.find()) {
            textStart = textEnd;
            textEnd = m.start();

            String s = text.substring(textStart, textEnd);
            if (!TextUtils.isEmpty(s)) {
                NewsDetailItem item = new NewsDetailItem();
                item.itemType = DetailTypeEnum.SPLASH_TEXT.getAsInt();
                item.articleUrl = url;
                item.content = s;// getStringFromHtml(s);
                if (!TextUtils.isEmpty(item.content)) {
                    items.add(item);
                }
            }

            textEnd = m.end();

            String img = m.group();
            Matcher m2 = pImg2.matcher(img);
            if (m2.find()) {
                img = m2.group().substring(SImgStart2.length());
                img = img.substring(0, img.length() - SImgEnd.length());
                NewsDetailItem item = new NewsDetailItem();
                item.articleUrl = url;
                item.itemType = DetailTypeEnum.IMAGE.getAsInt();
                item.itemUrl = img;
                item.content = img;
                items.add(item);
            }
        }

        textStart = textEnd;
        textEnd = text.length();
        String s = text.substring(textStart, textEnd);

        if (!TextUtils.isEmpty(s)) {
            NewsDetailItem item = new NewsDetailItem();
            item.articleUrl = url;
            item.itemType = DetailTypeEnum.SPLASH_TEXT.getAsInt();
            item.content = s;//getStringFromHtml(s);
            if (!TextUtils.isEmpty(item.content)) {
                items.add(item);
            }
        }

        return items;
    }
}
