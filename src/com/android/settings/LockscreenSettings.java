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

package com.android.settings;

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
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class LockscreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "LockscreenSettings";
    private ContentResolver resolver;

    private static final String KEY_SEE_THROUGH = "lockscreen_see_through";
    private static final String LOCKSCREEN_QUICK_UNLOCK_CONTROL = "lockscreen_quick_unlock_control";

    private CheckBoxPreference mSeeThrough;
    private CheckBoxPreference mQuickUnlockScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lockscreen_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mSeeThrough = (CheckBoxPreference) prefSet.findPreference(KEY_SEE_THROUGH);
        mSeeThrough.setChecked(Settings.System.getInt(resolver,
                Settings.System.LOCKSCREEN_SEE_THROUGH, 0) == 1);
        mSeeThrough.setOnPreferenceChangeListener(this);

	mQuickUnlockScreen = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_QUICK_UNLOCK_CONTROL);
	mQuickUnlockScreen.setChecked(Settings.System.getInt(resolver,
		Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, 0) == 1);
	mQuickUnlockScreen.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if (preference == mSeeThrough) {
            boolean newValue = (Boolean) value;
            Settings.System.putInt(getContentResolver(),
			Settings.System.LOCKSCREEN_SEE_THROUGH, newValue ? 1 : 0);
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
