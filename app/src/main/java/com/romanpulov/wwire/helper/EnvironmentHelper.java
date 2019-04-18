package com.romanpulov.wwire.helper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.support.annotation.NonNull;

@SuppressWarnings("unused")
public class EnvironmentHelper {

    public static boolean isSupportsES2(@NonNull Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        return configurationInfo.reqGlEsVersion >= 0x20000;
    }

    private EnvironmentHelper() {
        throw new AssertionError();
    }
}
