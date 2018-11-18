package com.green.grodnonews.mvp;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;

import com.green.grodnonews.room.BlackListItem;

import java.util.List;

public interface BlackListContract {
    interface View {
        LifecycleOwner getViewLifecycleOwner();

        Observer<List<BlackListItem>> getObserver();

        void processError(String errorText);
    }


    interface Presenter {
        void onCreateView(@NonNull Context context, @NonNull BlackListContract.View view);

        void onDestroyView(@NonNull BlackListContract.View view);


        void processError(String errorText);

        void addUserToBlackList(BlackListItem user);

        void delUserFromBlackList(String userName);

        void clearBlackList();
    }

    interface Repository {
        void addObserver(LifecycleOwner owner, Observer<List<BlackListItem>> observer);

        void removeObserver(LifecycleOwner owner);

        void addUserToBlackList(BlackListItem item);

        void delUser(String userName);

        void clearUserList();
    }
}
