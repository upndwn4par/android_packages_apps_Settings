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

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
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

import com.android.settings.graviton.colorpicker.ColorPickerPreference;

public class StatusbarSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "StatusbarSettings";
    private ContentResolver resolver;

    private static final String STATUS_BAR_TRAFFIC = "status_bar_traffic";
    private static final String PREF_STATUSBAR_SIGNAL_TEXT = "statusbar_signal_text";
    private static final String PREF_STATUSBAR_SIGNAL_TEXT_COLOR = "statusbar_signal_text_color";
    private static final String PREF_STATUS_BAR_TRAFFIC_COLOR = "status_bar_traffic_color";

    private CheckBoxPreference mStatusBarTraffic;
    private ListPreference mTextStyle;
    private ColorPickerPreference mSignalColor;
    private ColorPickerPreference mTrafficColor;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.statusbar_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

	mStatusBarTraffic = (CheckBoxPreference) findPreference(STATUS_BAR_TRAFFIC);
	mStatusBarTraffic.setChecked(Settings.System.getInt(getContentResolver(),
	Settings.System.STATUS_BAR_TRAFFIC, 0) == 1);

        mTextStyle = (ListPreference) findPreference(PREF_STATUSBAR_SIGNAL_TEXT);
        mTextStyle.setOnPreferenceChangeListener(this);
        mTextStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_SIGNAL_TEXT, 0)));

        mSignalColor = (ColorPickerPreference) findPreference(PREF_STATUSBAR_SIGNAL_TEXT_COLOR);
        mSignalColor.setOnPreferenceChangeListener(this);

        mTrafficColor = (ColorPickerPreference) findPreference(PREF_STATUS_BAR_TRAFFIC_COLOR);
        mTrafficColor.setOnPreferenceChangeListener(this);

    }



    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean value;

        if (preference == mStatusBarTraffic) {
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_TRAFFIC,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return true;
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mTextStyle) {
            int val = Integer.parseInt((String) value);
            int index = mTextStyle.findIndexOfValue((String) value);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_SIGNAL_TEXT, val);
            mTextStyle.setSummary(mTextStyle.getEntries()[index]);
            return true;
        } else if (preference == mSignalColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(value)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.STATUSBAR_SIGNAL_TEXT_COLOR, intHex);
            return true;
        } else if (preference == mTrafficColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(value)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_TRAFFIC_COLOR, intHex);
            return true;
        }
        return false;
    }
}
