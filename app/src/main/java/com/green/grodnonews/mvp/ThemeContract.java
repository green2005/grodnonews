package com.green.grodnonews.mvp;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;

public interface ThemeContract {
    interface View {
        LifecycleOwner getLifeCycleOwner();
    }

    interface Presenter {
        void setFontSize(Context context, int titleFontSize, int contentFontSize);
        void setTheme(int theme, Context context);
        int getTheme();
        void onViewCreated(Context context);
    }

}
