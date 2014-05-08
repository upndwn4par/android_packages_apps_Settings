/*
 *  Copyright (C) 2014 upndwn4par @ xda developers
 *  Large portions Copyright (C) 2013 The OmniROM Project
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

public class PowermenuSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "PowermenuSettings";
    private ContentResolver resolver;

    private static final String POWER_MENU_SETTINGS = "powermenu_settings";
    private static final String POWER_MENU_SCREENSHOT = "power_menu_screenshot";
    private static final String POWER_MENU_SCREENRECORD = "power_menu_screenrecord";
    private static final String POWER_MENU_MOBILE_DATA = "power_menu_mobile_data";
    private static final String POWER_MENU_AIRPLANE_MODE = "power_menu_airplane_mode";
    private static final String POWER_MENU_SOUND_TOGGLES = "power_menu_sound_toggles";

    private CheckBoxPreference mScreenshotPowerMenu;
    private CheckBoxPreference mScreenrecordPowerMenu;
    private CheckBoxPreference mMobileDataPowerMenu;
    private CheckBoxPreference mAirplaneModePowerMenu;
    private CheckBoxPreference mSoundTogglesPowerMenu;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.powermenu_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

	mScreenshotPowerMenu = (CheckBoxPreference) prefSet.findPreference(POWER_MENU_SCREENSHOT);
	mScreenshotPowerMenu.setChecked(Settings.System.getInt(resolver,
		Settings.System.SCREENSHOT_IN_POWER_MENU, 0) == 1);
	mScreenshotPowerMenu.setOnPreferenceChangeListener(this);

        mScreenrecordPowerMenu = (CheckBoxPreference) prefSet.findPreference(POWER_MENU_SCREENRECORD);
        if (!getResources().getBoolean(com.android.internal.R.bool.config_enableScreenrecordChord)) {
            PreferenceGroup powerMenuCategory = (PreferenceGroup)
                findPreference(POWER_MENU_SETTINGS);
            powerMenuCategory.removePreference(mScreenrecordPowerMenu);
        } else {
            mScreenrecordPowerMenu.setChecked(Settings.System.getInt(resolver,
                    Settings.System.SCREENRECORD_IN_POWER_MENU, 0) == 1);
            mScreenrecordPowerMenu.setOnPreferenceChangeListener(this);
        }

        mMobileDataPowerMenu = (CheckBoxPreference) prefSet.findPreference(POWER_MENU_MOBILE_DATA);
        Context context = getActivity();
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.isNetworkSupported(ConnectivityManager.TYPE_MOBILE)) {
            mMobileDataPowerMenu.setChecked(Settings.System.getInt(resolver,
                Settings.System.MOBILE_DATA_IN_POWER_MENU, 0) == 1);
            mMobileDataPowerMenu.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mMobileDataPowerMenu);
        }

        mAirplaneModePowerMenu = (CheckBoxPreference) prefSet.findPreference(POWER_MENU_AIRPLANE_MODE);
        mAirplaneModePowerMenu.setChecked(Settings.System.getInt(resolver,
                Settings.System.AIRPLANE_MODE_IN_POWER_MENU, 1) == 1);
        mAirplaneModePowerMenu.setOnPreferenceChangeListener(this);

        mSoundTogglesPowerMenu = (CheckBoxPreference) prefSet.findPreference(POWER_MENU_SOUND_TOGGLES);
        mSoundTogglesPowerMenu.setChecked(Settings.System.getInt(resolver,
                Settings.System.SOUND_TOGGLES_IN_POWER_MENU, 1) == 1);
        mSoundTogglesPowerMenu.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if (preference == mScreenshotPowerMenu) {
	    boolean newValue = (Boolean) value;
	    Settings.System.putInt(getContentResolver(),
			Settings.System.SCREENSHOT_IN_POWER_MENU, newValue ? 1 : 0);
        } else if (preference == mScreenrecordPowerMenu) {
            boolean newValue = (Boolean) value;
            Settings.System.putInt(getContentResolver(),
			Settings.System.SCREENRECORD_IN_POWER_MENU, newValue ? 1 : 0);
        } else if (preference == mMobileDataPowerMenu) {
            boolean newValue = (Boolean) value;
            Settings.System.putInt(getContentResolver(),
			Settings.System.MOBILE_DATA_IN_POWER_MENU, newValue ? 1 : 0);
        } else if (preference == mAirplaneModePowerMenu) {
            boolean newValue = (Boolean) value;
            Settings.System.putInt(getContentResolver(),
			Settings.System.AIRPLANE_MODE_IN_POWER_MENU, newValue ? 1 : 0);
        } else if (preference == mSoundTogglesPowerMenu) {
            boolean newValue = (Boolean) value;
            Settings.System.putInt(getContentResolver(),
			Settings.System.SOUND_TOGGLES_IN_POWER_MENU, newValue ? 1 : 0);
        } else {
            return false;
        }
        return true;
    }
}
