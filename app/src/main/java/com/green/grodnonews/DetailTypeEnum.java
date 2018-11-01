package com.green.grodnonews;

public enum DetailTypeEnum {

    TITLE(0), IMAGE(1), VIDEO(2), SPLASH_TEXT(3), COMMENT(4), COMMENT_TITLE(5);

    private int mType;

    DetailTypeEnum(int i) {
        mType = i;
    }

    public int getAsInt() {
        return mType;
    }

    public static DetailTypeEnum getTypeById(int id) {
        for (DetailTypeEnum d : DetailTypeEnum.values()) {
            if (d.getAsInt() == id) {
                return d;
            }
        }
        return TITLE;
    }

    }
