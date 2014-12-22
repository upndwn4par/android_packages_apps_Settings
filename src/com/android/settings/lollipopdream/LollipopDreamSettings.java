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

    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final String STATUS_BAR_BATTERY_STYLE_HIDDEN = "4";
    private static final String STATUS_BAR_BATTERY_STYLE_TEXT = "6";

    private ListPreference mStatusBarBattery;
    private ListPreference mStatusBarBatteryShowPercent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lpd_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

	mStatusBarBattery = (ListPreference) findPreference(STATUS_BAR_BATTERY_STYLE);
	mStatusBarBatteryShowPercent =
	(ListPreference) findPreference(STATUS_BAR_SHOW_BATTERY_PERCENT);

        int batteryStyle = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_BATTERY_STYLE, 0);
        mStatusBarBattery.setValue(String.valueOf(batteryStyle));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
        mStatusBarBattery.setOnPreferenceChangeListener(this);

	int batteryShowPercent = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0);
	mStatusBarBatteryShowPercent.setValue(String.valueOf(batteryShowPercent));
	mStatusBarBatteryShowPercent.setSummary(mStatusBarBatteryShowPercent.getEntry());
	mStatusBarBatteryShowPercent.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
	ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mStatusBarBattery) {
            int batteryStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarBattery.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, Settings.System.STATUS_BAR_BATTERY_STYLE, batteryStyle);
            mStatusBarBattery.setSummary(mStatusBarBattery.getEntries()[index]);
	    enableStatusBarBatteryDependents((String) newValue);
	} else if (preference == mStatusBarBatteryShowPercent) {
	    int batteryShowPercent = Integer.valueOf((String) newValue);
	    int index = mStatusBarBatteryShowPercent.findIndexOfValue((String) newValue);
	    Settings.System.putInt(
	    	    resolver, Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, batteryShowPercent);
	    mStatusBarBatteryShowPercent.setSummary(mStatusBarBatteryShowPercent.getEntries()[index]);
        } else {
            return false;
        }
        return true;
    }

    private void enableStatusBarBatteryDependents(String value) {
	boolean enabled = !(value.equals(STATUS_BAR_BATTERY_STYLE_TEXT)
		|| value.equals(STATUS_BAR_BATTERY_STYLE_HIDDEN));
	mStatusBarBatteryShowPercent.setEnabled(enabled);
    }
}
