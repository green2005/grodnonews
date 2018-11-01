package com.green.grodnonews.loader;

import android.content.Context;


import com.green.grodnonews.App;
import com.green.grodnonews.ErrorHelper;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Executor {


    private ThreadPoolExecutor mExecutor;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    public static final String KEY = "Executor";

    public Executor() {
        mExecutor = new ThreadPoolExecutor(CPU_COUNT, CPU_COUNT, 0L, TimeUnit.MILLISECONDS, new LIFOLinkedBlockingDeque<Runnable>());
    }

    public static Executor getExecutor(Context context) {
        try {
            return (Executor) App.get(Executor.KEY);
        } catch (Exception e) {
            ErrorHelper.processError(context, e.getMessage());
        }
        return null;
    }

    public void start(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }
        mExecutor.execute(runnable);
    }

    public boolean remove(Runnable runnable) {
        if (runnable == null)
            throw new IllegalArgumentException("Runnable cannot be null");
        return mExecutor.remove(runnable);
    }
}
