package com.green.grodnonews.blogio;

import android.content.Context;
import android.content.SharedPreferences;


public class AccountSettings {
    private String mUserName;
    private String mPwd;
    private static final String PREF_ACCOUNT_SETTINGS = "ACCOUNT";
    private static final String PREF_USER_NAME = "USERNAME";
    private static final String PREF_PWD = "PWD";

    public AccountSettings(Context context) {
        if (context != null) {
            readSettings(context);
        }
    }


    public String getUserName() {
        return mUserName;
    }

    public String getPwd() {
        return mPwd;
    }


    public void saveSettings(String userName, String pwd, Context context) {
        mPwd = pwd;
        mUserName = userName;
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_ACCOUNT_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putString(PREF_USER_NAME, mUserName);
        editor.putString(PREF_PWD, mPwd);
        editor.apply();
    }

    private void readSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_ACCOUNT_SETTINGS, Context.MODE_PRIVATE);
        if (preferences.contains(PREF_USER_NAME)) {
            mUserName = preferences.getString(PREF_USER_NAME, "");
            mPwd = preferences.getString(PREF_PWD, "");
        }
    }


}
