package com.green.grodnonews.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.green.grodnonews.R;
import com.green.grodnonews.mvp.ThemePresenter;


public class PreferencesActivity extends AppCompatActivity {
    private ThemePresenter mThemePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemePresenter = ViewModelProviders.of(this).get(ThemePresenter.class);
        mThemePresenter.onViewCreated(this);
        setTheme(mThemePresenter.getTheme());

        super.onCreate(savedInstanceState);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prefFragment = PrefFragment.getNewFragment(new Bundle());
        ft.replace(android.R.id.content , prefFragment);
        ft.commit();
        setTitle(R.string.prefTitle);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==  android.R.id.home){
            finish();
            return true;
        } else
        return super.onOptionsItemSelected(item);
    }
}
