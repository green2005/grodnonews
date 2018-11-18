package com.green.grodnonews.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.green.grodnonews.R;
import com.green.grodnonews.blogio.AccountSettings;
import com.green.grodnonews.blogio.AuthDialog;

public class PrefFragment extends PreferenceFragment {
    private static final String MARKET_URL = "market://details?id=";


    public static Fragment getNewFragment(Bundle args) {
        Fragment prefFragment = new PrefFragment();
        prefFragment.setArguments(args);
        return prefFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
        String key = preference.getKey();
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        Activity activity = getActivity();
        if (activity == null) {
            return false;
        }


        if (key.equalsIgnoreCase(activity.getString(R.string.rate_key))) {
            rateMe();
        } else if (key.equalsIgnoreCase(activity.getString(R.string.blacklist_key))) {
            showBlackList();
        } else if (key.equalsIgnoreCase(activity.getString(R.string.authenticate_key))) {
            authenticate();
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    private void authenticate() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        AccountSettings settings = new AccountSettings(activity);
        AuthDialog.editAuth(activity, settings, null);
    }

    private void showBlackList(){
        Intent i = new Intent(getActivity(), BlackListEditActivity.class);
        startActivity(i);
    }

    private void rateMe() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Uri uri = Uri.parse(MARKET_URL + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            //ErrorHelper.showErrorDialog(R.string.errormarket, activity, null);
        }
    }
}
