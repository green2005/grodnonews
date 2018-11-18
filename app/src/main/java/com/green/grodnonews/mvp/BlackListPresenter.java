package com.green.grodnonews.mvp;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.green.grodnonews.room.BlackListItem;

public class BlackListPresenter extends ViewModel implements BlackListContract.Presenter {
    BlackListRepository mRepository;
    BlackListContract.View mBlackListContractView;

    @Override
    public void onCreateView(@NonNull Context context, @NonNull BlackListContract.View view) {
        mRepository = new BlackListRepository(context.getApplicationContext());
        mRepository.addObserver(view.getViewLifecycleOwner(), view.getObserver());
        mBlackListContractView = view;
    }

    @Override
    public void onDestroyView(@NonNull BlackListContract.View view) {
        mRepository.removeObserver(mBlackListContractView.getViewLifecycleOwner());
        mBlackListContractView = null;
    }

    @Override
    public void processError(String errorText) {
        if (mBlackListContractView != null) {
            mBlackListContractView.processError(errorText);
        }
    }

    @Override
    public void addUserToBlackList(BlackListItem user) {
        mRepository.addUserToBlackList(user);
    }

    @Override
    public void delUserFromBlackList(String userName) {
        mRepository.delUser(userName);
    }

    @Override
    public void clearBlackList() {
        mRepository.clearUserList();
    }
}
