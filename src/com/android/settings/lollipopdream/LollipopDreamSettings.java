/* Copyright (C) 2014 upndwn4par
**
** LPD = Lollipop Dream
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

package com.android.settings.lollipopdream;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.DisplayInfo;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;

import java.util.ArrayList;
import java.util.List;

public class LollipopDreamSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "LollipopDreamSettings";
    private ContentResolver resolver;

    private static final String KILL_APP_LONGPRESS_BACK = "kill_app_longpress_back";
    public static final String VOLUME_WAKE_SCREEN = "volume_wake_screen";
    private static final String LOCKSCREEN_QUICK_UNLOCK_CONTROL = "lockscreen_quick_unlock_control";

    private CheckBoxPreference mKillAppLongpressBack;
    private CheckBoxPreference mVolumeWakeScreen;
    private SwitchPreference mQuickUnlockScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lpd_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

	mKillAppLongpressBack = (CheckBoxPreference) findPreference(KILL_APP_LONGPRESS_BACK);
	mKillAppLongpressBack.setChecked(Settings.System.getInt(getContentResolver(),
		Settings.System.KILL_APP_LONGPRESS_BACK, 0) == 1);
	mKillAppLongpressBack.setOnPreferenceChangeListener(this);

	mVolumeWakeScreen = (CheckBoxPreference) prefSet.findPreference(VOLUME_WAKE_SCREEN);
	mVolumeWakeScreen.setChecked(Settings.System.getInt(resolver,
		Settings.System.VOLUME_WAKE_SCREEN, 0) == 1);
	mVolumeWakeScreen.setOnPreferenceChangeListener(this);

	mQuickUnlockScreen = (SwitchPreference) prefSet.findPreference(LOCKSCREEN_QUICK_UNLOCK_CONTROL);
	mQuickUnlockScreen.setChecked(Settings.System.getInt(resolver,
		Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, 0) == 1);
	mQuickUnlockScreen.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if (preference == mKillAppLongpressBack) {
	    boolean newValue = (Boolean) value;
	    Settings.System.putInt(getContentResolver(),
		    Settings.System.KILL_APP_LONGPRESS_BACK, newValue ? 1 : 0);
	} else if (preference == mVolumeWakeScreen) {
	    boolean newValue = (Boolean) value;
	    Settings.System.putInt(getContentResolver(),
		    Settings.System.VOLUME_WAKE_SCREEN, newValue ? 1 : 0);
	} else if (preference == mQuickUnlockScreen) {
	    boolean newValue = (Boolean) value;
	    Settings.System.putInt(getContentResolver(),
		    Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, newValue ? 1 : 0);
        } else {
            return false;
        }
        return true;
    }
}
