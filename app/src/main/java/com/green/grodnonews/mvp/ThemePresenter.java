package com.green.grodnonews.mvp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.green.grodnonews.R;

import static android.content.Context.MODE_PRIVATE;

public class ThemePresenter extends ViewModel implements ThemeContract.Presenter {
    private static final String PREF_THEME_SETTINGS = "THEME_SETTINGS";
    private static final String THEME_KEY = "THEME_KEY";
    private static final String TITLE_FONT_SIZE_KEY = "TITLE_KEY";
    private static final String CONTENT_FONT_SIZE_KEY = "CONTENT_KEY";


    private int mTheme;
    private MutableLiveData<Integer> mTitleFontSize = new MutableLiveData<>();
    private MutableLiveData<Integer> mContentFontSize = new MutableLiveData<>();

    public LiveData<Integer> getContentFontSize() {
        return mContentFontSize;
    }

    public LiveData<Integer> getTitleFontSize() {
        return mTitleFontSize;
    }


    @Override
    public void setFontSize(Context context, int titleFontSize, int contentFontSize) {
        mTitleFontSize.setValue(titleFontSize);
        mContentFontSize.setValue(contentFontSize);
        writeSettings(context);
    }

    @Override
    public void setTheme(int theme, Context context) {
        mTheme = theme;
        writeSettings(context);
        restartApp(context);
    }

    private void restartApp(Context context){
        Intent i = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }


    @Override
    public int getTheme() {
        return mTheme;
    }

    @Override
    public void onViewCreated(Context context) {
        readSettings(context);

    }

    private void readSettings(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(PREF_THEME_SETTINGS, MODE_PRIVATE);
        mTheme = prefs.getInt(THEME_KEY, R.style.ActivityTheme_Primary_Base_Light);
        if (mTitleFontSize == null) {
            mTitleFontSize = new MutableLiveData<>();
        }

        int textSize = (int) context.getResources().getDimension(R.dimen.textSize);
        textSize = prefs.getInt(CONTENT_FONT_SIZE_KEY, textSize);
        mContentFontSize.setValue(textSize);

        int titleSize = (int) context.getResources().getDimension(R.dimen.titleSize);
        textSize = prefs.getInt(TITLE_FONT_SIZE_KEY, titleSize);
        mTitleFontSize.setValue(textSize);
    }

    private void writeSettings(Context context) {
        SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(PREF_THEME_SETTINGS, MODE_PRIVATE).edit();
        editor.putInt(THEME_KEY, mTheme);
        editor.putInt(CONTENT_FONT_SIZE_KEY, mContentFontSize.getValue());
        editor.putInt(TITLE_FONT_SIZE_KEY, mTitleFontSize.getValue());
        editor.apply();
    }

}
