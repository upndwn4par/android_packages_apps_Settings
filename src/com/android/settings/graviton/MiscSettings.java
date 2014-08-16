/*
 *  Copyright (C) 2014 upndwn4par @ xda developers
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.android.settings.graviton;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.DisplayInfo;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;

import java.util.ArrayList;
import java.util.List;

public class MiscSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "MiscSettings";
    private ContentResolver resolver;

    private static final String KILL_APP_LONGPRESS_BACK = "kill_app_longpress_back";
    private static final String RECENT_MENU_CLEAR_ALL = "recent_menu_clear_all";
    private static final String RECENT_MENU_CLEAR_ALL_LOCATION = "recent_menu_clear_all_location";

    private CheckBoxPreference mKillAppLongpressBack;
    private CheckBoxPreference mRecentClearAll;
    private ListPreference mRecentClearAllPosition;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.misc_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

	mKillAppLongpressBack = (CheckBoxPreference) findPreference(KILL_APP_LONGPRESS_BACK);
	mKillAppLongpressBack.setChecked(Settings.System.getInt(getContentResolver(),
		Settings.System.KILL_APP_LONGPRESS_BACK, 0) == 1);
	mKillAppLongpressBack.setOnPreferenceChangeListener(this);

        mRecentClearAll = (CheckBoxPreference) prefSet.findPreference(RECENT_MENU_CLEAR_ALL);
        mRecentClearAll.setChecked(Settings.System.getInt(resolver,
               Settings.System.SHOW_CLEAR_RECENTS_BUTTON, 1) == 1);
        mRecentClearAll.setOnPreferenceChangeListener(this);
        mRecentClearAllPosition = (ListPreference) prefSet.findPreference(RECENT_MENU_CLEAR_ALL_LOCATION);
        String recentClearAllPosition = Settings.System.getString(resolver, Settings.System.CLEAR_RECENTS_BUTTON_LOCATION);
        if (recentClearAllPosition != null) {
             mRecentClearAllPosition.setValue(recentClearAllPosition);
        }
        mRecentClearAllPosition.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
	ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mKillAppLongpressBack) {
	    boolean newValue = (Boolean) value;
	    Settings.System.putInt(getContentResolver(),
		    Settings.System.KILL_APP_LONGPRESS_BACK, newValue ? 1 : 0);
        } else if (preference == mRecentClearAll) {
            boolean newValue = (Boolean) value;
            Settings.System.putInt(getContentResolver(),
		    Settings.System.SHOW_CLEAR_RECENTS_BUTTON, newValue ? 1 : 0);
        } else if (preference == mRecentClearAllPosition) {
            String newValue = (String) value;
            Settings.System.putString(getContentResolver(),
		    Settings.System.CLEAR_RECENTS_BUTTON_LOCATION, newValue);
        } else {
            return false;
        }
        return true;
    }
}
