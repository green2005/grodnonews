package com.green.grodnonews.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.R;
import com.green.grodnonews.ui.NewsFeedFragment;

public class TabsAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public TabsAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle b = new Bundle();
        b.putInt(NewsFeedFragment.FRAGMENT_NUMBER_KEY, position);
        return NewsFeedFragment.getFragment(b);
    }

    @Override
    public int getCount() {
        return FeedTypeEnum.values().length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        FeedTypeEnum item = FeedTypeEnum.getTypeById(position);
        switch (item){
            case S13:{
                return mContext.getString(R.string.S13title);
            }
            case HRODNALIFE:{
                return mContext.getString(R.string.HrodnaLifeTitle);
            }
            case NEWGRODNO:{
                return mContext.getString(R.string.NewGrodnoTitle);
            }
        }
        return null;
    }


}
