package com.green.grodnonews.mvp;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;

import com.green.grodnonews.App;
import com.green.grodnonews.room.BlackListItem;
import com.green.grodnonews.room.NewsDB;

import java.util.List;

public class BlackListRepository implements BlackListContract.Repository {
    private NewsDB mDb;

    BlackListRepository(Context appContext) {
        mDb = ((App) appContext).getDB();
    }

    @Override
    public void addObserver(LifecycleOwner owner, Observer<List<BlackListItem>> observer) {
        mDb.blackListDao().getBlackList().observe(owner, observer);
    }

    @Override
    public void removeObserver(LifecycleOwner owner) {
        mDb.blackListDao().getBlackList().removeObservers(owner);
    }


    @Override
    public void addUserToBlackList(BlackListItem item) {
        mDb.blackListDao().insertRecord(item);
    }

    @Override
    public void delUser(final String userName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDb.blackListDao().removeFromBlackList(userName);
            }
        }).start();
    }

    @Override
    public void clearUserList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDb.blackListDao().clearBlackList();
            }
        }).start();
    }
}
