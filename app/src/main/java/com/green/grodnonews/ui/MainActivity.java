package com.green.grodnonews.ui;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Switch;

import com.green.grodnonews.R;
import com.green.grodnonews.adapters.TabsAdapter;
import com.green.grodnonews.mvp.ThemeContract;
import com.green.grodnonews.mvp.ThemePresenter;

public class MainActivity extends AppCompatActivity implements ThemeContract.View {
    Toolbar mToolbar;
    ThemePresenter mThemePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemePresenter = ViewModelProviders.of(this).get(ThemePresenter.class);
        mThemePresenter.onViewCreated(this);
        setTheme(mThemePresenter.getTheme());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.app_name));
        // mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(
                new TabsAdapter(getSupportFragmentManager(), MainActivity.this));
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void showFontPopup() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.popup_font_size, null);
        Switch nightModeSwitch = popupView.findViewById(R.id.switch_night_mode);
        if (mThemePresenter.getTheme() == R.style.Activity_Base_Dark) {
            nightModeSwitch.setChecked(true);
        } else {
            nightModeSwitch.setChecked(false);
        }
        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mThemePresenter.setTheme(R.style.Activity_Base_Dark, MainActivity.this);
                } else {
                    mThemePresenter.setTheme(R.style.ActivityTheme_Primary_Base_Light, MainActivity.this);
                }
            }
        });

        ImageButton fontLargeBtn = popupView.findViewById(R.id.btn_font_large);
        fontLargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int contentSize = mThemePresenter.getContentFontSize().getValue() + 1;
                int titleSize = mThemePresenter.getTitleFontSize().getValue() + 1;
                mThemePresenter.setFontSize(MainActivity.this, titleSize, contentSize);
            }
        });

        ImageButton fontSmallBtn = popupView.findViewById(R.id.btn_font_small);
        fontSmallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int contentSize = mThemePresenter.getContentFontSize().getValue() - 1;
                int titleSize = mThemePresenter.getTitleFontSize().getValue() - 1;
                mThemePresenter.setFontSize(MainActivity.this, titleSize, contentSize);
            }
        });


        PopupWindow w = new PopupWindow(popupView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        w.setElevation(5.0f);
        w.setFocusable(true);
        w.showAsDropDown(mToolbar, mToolbar.getWidth() - 10, mToolbar.getHeight() + 10);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.change_font) {
            showFontPopup();
        } else if (item.getItemId() == R.id.id_options) {
            Intent intent = new Intent(this, PreferencesActivity.class );
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public LifecycleOwner getLifeCycleOwner() {
        return this;
    }
}
