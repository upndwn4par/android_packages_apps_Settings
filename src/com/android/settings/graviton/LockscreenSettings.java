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
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.DisplayInfo;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;

import java.util.ArrayList;
import java.util.List;

public class LockscreenSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "LockscreenSettings";
    private ContentResolver resolver;

    public static final String VOLUME_WAKE_SCREEN = "volume_wake_screen";
    private static final String KEY_SEE_THROUGH = "lockscreen_see_through";
    private static final String KEY_PEEK = "notification_peek";
    private static final String PEEK_APPLICATION = "com.jedga.peek";
    private static final String ALLOW_ALL_LOCKSCREEN_WIDGETS = "allow_all_lockscreen_widgets";
    private static final String HOVER_DISABLE_ON_LOCKSCREEN = "hover_disable_on_lockscreen";

    private CheckBoxPreference mVolumeWakeScreen;
    private CheckBoxPreference mSeeThrough;
    private CheckBoxPreference mNotificationPeek;
    private CheckBoxPreference mAllWidgets;
    private CheckBoxPreference mHoverLockscreenDisable;

    private PackageStatusReceiver mPackageStatusReceiver;
    private IntentFilter mIntentFilter;


    private boolean isPeekAppInstalled() {
	return isPackageInstalled(PEEK_APPLICATION);
    }

    private boolean isPackageInstalled(String packagename) {
	PackageManager pm = getActivity().getPackageManager();
	try {
	    pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
	    return true;
	} catch (NameNotFoundException e) {
	    return false;
	}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lockscreen_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

	mVolumeWakeScreen = (CheckBoxPreference) prefSet.findPreference(VOLUME_WAKE_SCREEN);
	mVolumeWakeScreen.setChecked(Settings.System.getInt(resolver,
		Settings.System.VOLUME_WAKE_SCREEN, 0) == 1);
	mVolumeWakeScreen.setOnPreferenceChangeListener(this);

	mSeeThrough = (CheckBoxPreference) findPreference(KEY_SEE_THROUGH);
	mSeeThrough.setChecked(Settings.System.getInt(getContentResolver(),
		Settings.System.LOCKSCREEN_SEE_THROUGH, 0) == 1);
	mSeeThrough.setOnPreferenceChangeListener(this);

	mAllWidgets = (CheckBoxPreference) findPreference(ALLOW_ALL_LOCKSCREEN_WIDGETS);
	mAllWidgets.setChecked(Settings.System.getInt(getContentResolver(),
		Settings.System.ALLOW_ALL_LOCKSCREEN_WIDGETS, 0) == 1);
	mAllWidgets.setOnPreferenceChangeListener(this);

	mHoverLockscreenDisable = (CheckBoxPreference) findPreference(HOVER_DISABLE_ON_LOCKSCREEN);
	mHoverLockscreenDisable.setChecked(Settings.System.getInt(getContentResolver(),
		Settings.System.HOVER_DISABLE_ON_LOCKSCREEN, 0) == 1);
	mHoverLockscreenDisable.setOnPreferenceChangeListener(this);

	mNotificationPeek = (CheckBoxPreference) findPreference(KEY_PEEK);
	mNotificationPeek.setPersistent(false);

        if (mPackageStatusReceiver == null) {
            mPackageStatusReceiver = new PackageStatusReceiver();
        }
        if (mIntentFilter == null) {
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            mIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        }
        getActivity().registerReceiver(mPackageStatusReceiver, mIntentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mPackageStatusReceiver, mIntentFilter);

        updateState();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mPackageStatusReceiver);
    }

    private void updateState() {
        updatePeekCheckbox();
    }

    private void updatePeekCheckbox() {
        boolean enabled = Settings.System.getInt(getContentResolver(),
                Settings.System.PEEK_STATE, 0) == 1;
        mNotificationPeek.setChecked(enabled && !isPeekAppInstalled());
        mNotificationPeek.setEnabled(!isPeekAppInstalled());
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mNotificationPeek) {
            Settings.System.putInt(getContentResolver(), Settings.System.PEEK_STATE,
                    mNotificationPeek.isChecked() ? 1 : 0);
	}
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
	if (preference == mVolumeWakeScreen) {
	    boolean newValue = (Boolean) value;
	    Settings.System.putInt(getContentResolver(),
			Settings.System.VOLUME_WAKE_SCREEN, newValue ? 1 : 0);
	} else if (preference == mSeeThrough) {
	    boolean newValue = (Boolean) value;
	    Settings.System.putInt(getContentResolver(),
			Settings.System.LOCKSCREEN_SEE_THROUGH, newValue ? 1 : 0);
	} else if (preference == mAllWidgets) {
	    boolean newValue = (Boolean) value;
	    Settings.System.putInt(getContentResolver(),
			Settings.System.ALLOW_ALL_LOCKSCREEN_WIDGETS, newValue ? 1 : 0);
	} else if (preference == mHoverLockscreenDisable) {
	    boolean newValue = (Boolean) value;
	    Settings.System.putInt(getContentResolver(),
			Settings.System.HOVER_DISABLE_ON_LOCKSCREEN, newValue ? 1 : 0);
        } else {
            return false;
        }
        return true;
    }

    public class PackageStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                updatePeekCheckbox();
            } else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                updatePeekCheckbox();
            }
        }
    }
}
