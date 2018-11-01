package com.green.grodnonews;

import android.location.Location;

import java.util.Locale;
import java.util.Random;

public class API {
    private static final String APPLICATION_ACCESS_TOKEN[] =
            {"4482471944824719448247196344c48d2544482448247191fceab0c1c9b6487ef990f56",
                    "8e3dc1288e3dc1288e3dc1280e8e7bc79a88e3d8e3dc128d57392eb553f801c42704021",
                    "db212f04db212f04db212f04badb62c78eddb21db212f0480085ae2095155747d24b514",
                    "afb0591fafb0591fafb0591f8eaff38122aafb0afb0591ff4fe0d96cf62fb7dc0dc47a0",
                    "5ffbcea05ffbcea05ffbcea0a55fc8425f55ffb5ffbcea00465768aa04253d5f0f7a23f",
                    "aba190e5aba190e5aba190e502abc7b771aaba1aba190e5f03f2a86f916f1fa0cabdddb"
            };
    private static final String S13_FEED_URL = "http://s13.ru/feed?paged=%d";

    private static final String RESTORE_KEY = "restore_key";

    private static final String URL_KEY = "url_key";
    private static final String VK_VERSION = "5.80";
    private static final String VK_S13_ID = "-51743326";
    private static final String VK_HRODNA_LIFE_ID = "-83323241";
    private static final String VK_NEWGRODNO_ID = "-101660797";

    public static final String YOUTUBE_KEY = "AIzaSyBKDF2bJKzFu2BHUc9IQ7gx3-HWdbQGJUo";
    //"AIzaSyANkXGb2yUidzMGWeweb5NAVn5a3sjO6vs";


    static final int DATA_LIMIT = 15;


    private static final String VK_FEED_URL =
            "https://api.vk.com/method/wall.get?owner_id=%s&count=%s&filter=owner&extended=1&v=%s&access_token=%s&offset=%d";


    public static String getFeedUrl(final int start_from, final FeedTypeEnum newsFeedTypeEnum) {
        int offset = start_from * DATA_LIMIT;
        switch (newsFeedTypeEnum) {
            case NEWGRODNO: {
                int i = new Random().nextInt(APPLICATION_ACCESS_TOKEN.length);
                String token = APPLICATION_ACCESS_TOKEN[i];
                return String.format(Locale.ROOT, VK_FEED_URL, VK_NEWGRODNO_ID, DATA_LIMIT, VK_VERSION, token, offset);
            }
            case S13: {

                return String.format(Locale.ROOT, S13_FEED_URL , start_from + 1) ;

               //do not use vk
                // int i = new Random().nextInt(APPLICATION_ACCESS_TOKEN.length);
               // String token = APPLICATION_ACCESS_TOKEN[i];
               // return String.format(Locale.ROOT, VK_FEED_URL, VK_S13_ID, DATA_LIMIT, VK_VERSION, token, offset);

            }
            case HRODNALIFE: {
                int i = new Random().nextInt(APPLICATION_ACCESS_TOKEN.length);
                String token = APPLICATION_ACCESS_TOKEN[i];
                return String.format(Locale.ROOT, VK_FEED_URL, VK_HRODNA_LIFE_ID, DATA_LIMIT, VK_VERSION, token, offset);
            }
            default: {
                return "";
            }
        }

    }

}
