package com.green.grodnonews;

import android.content.Context;
import android.widget.Toast;

public class ErrorHelper {

    public static void processError(Context context, String error){
        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    }
}
