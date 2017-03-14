package com.utlife.core.utils;

import android.content.Context;
import android.os.Build;
import android.support.compat.BuildConfig;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.logging.Logger;

/**
 * Created by xuqiang on 2017/1/4.
 */

public class ToastUtils {


    public static void showShort(Context context, String message) {
        boolean isEnable = NotificationManagerCompat.from(context).areNotificationsEnabled();
        if (!isEnable) {
            Log.e("ToastUtil", "通知权限未开启");
            return;
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, String message) {
        boolean isEnable = NotificationManagerCompat.from(context).areNotificationsEnabled();
        if (!isEnable) {
            Log.e("ToastUtil", "通知权限未开启");
            return;
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
