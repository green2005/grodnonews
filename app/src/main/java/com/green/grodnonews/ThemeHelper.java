package com.green.grodnonews;

import android.content.Context;
import android.os.Build;

public class ThemeHelper {
    public static int getColor(Context context, int id){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            return context.getColor(id);
        }else
        {
            return context.getResources().getColor(id);
        }
    }

}
