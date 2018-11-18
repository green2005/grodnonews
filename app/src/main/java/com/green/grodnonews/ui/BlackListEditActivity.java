package com.green.grodnonews.ui;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.green.grodnonews.R;
import com.green.grodnonews.adapters.BlackListAdapter;
import com.green.grodnonews.mvp.BlackListContract;
import com.green.grodnonews.mvp.BlackListPresenter;
import com.green.grodnonews.mvp.ThemePresenter;
import com.green.grodnonews.room.BlackListItem;

import java.util.ArrayList;
import java.util.List;

public class BlackListEditActivity extends AppCompatActivity implements BlackListContract.View, Observer<List<BlackListItem>> {


    private static final String PERSONS_KEY = "persons_key";
    private BlackListContract.Presenter mBlackListPresenter;
    private BlackListAdapter mAdapter;
    private TextView mTvEmpty;
    private MenuItem mDelBtn = null;
    private ArrayList<String> mSelectedUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePresenter themePresenter = ViewModelProviders.of(this).get(ThemePresenter.class);
        themePresenter.onViewCreated(this);
        setTheme(themePresenter.getTheme());


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_black_list);
        mSelectedUsers = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBlackListPresenter = ViewModelProviders.of(this).get(BlackListPresenter.class);
        mBlackListPresenter.onCreateView(this, this);
        mAdapter = new BlackListAdapter(this);
        mAdapter.setSelectedList(mSelectedUsers, new BlackListAdapter.OnSelectedItemsChangeListener() {
            @Override
            public void onChange() {
                setdelBtnEnabled(mSelectedUsers.size() > 0);
            }
        });
        mTvEmpty = findViewById(R.id.labelListEmpty);
        mTvEmpty.setVisibility(View.VISIBLE);
        ListView lvBlackList = findViewById(R.id.lvBlackList);
        lvBlackList.setAdapter(mAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(PERSONS_KEY, mSelectedUsers);
    }

    @Override
    protected void onDestroy() {
        mBlackListPresenter.onDestroyView(this);
        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(PERSONS_KEY)) {
            ArrayList<String> items = savedInstanceState.getStringArrayList(PERSONS_KEY);
            mSelectedUsers.clear();
            mSelectedUsers.addAll(items);
            mAdapter.notifyDataSetChanged();
            setdelBtnEnabled(mSelectedUsers.size() > 0);
        }
    }

    private void setdelBtnEnabled(Boolean enabled) {
        if (mDelBtn != null) {
            mDelBtn.setEnabled(enabled);
            if (enabled) {
                mDelBtn.getIcon().setAlpha(255);
            } else {
                mDelBtn.getIcon().setAlpha(80);
            }
        }
    }

    @Override
    public LifecycleOwner getViewLifecycleOwner() {
        return this;
    }

    @Override
    public Observer<List<BlackListItem>> getObserver() {
        return this;
    }

    @Override
    public void processError(String errorText) {
        Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChanged(@Nullable List<BlackListItem> blackListItems) {
        mAdapter.setItems(blackListItems);
        if ((blackListItems == null) || (blackListItems.size() == 0)) {
            mTvEmpty.setVisibility(View.VISIBLE);
        } else {
            mTvEmpty.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_blacklist, menu);
        mDelBtn = menu.findItem(R.id.delBtn);
        setdelBtnEnabled(mSelectedUsers.size() > 0);
        return true;
    }

    private void delSelectedUsers() {
        for (String item : mSelectedUsers) {
            mBlackListPresenter.delUserFromBlackList(item);
        }
        mSelectedUsers.clear();
        setdelBtnEnabled(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.delBtn) {
            delSelectedUsers();
        }
        return super.onOptionsItemSelected(item);
    }
}
