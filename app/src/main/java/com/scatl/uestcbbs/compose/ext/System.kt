package com.scatl.uestcbbs.compose.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.annotation.ChecksSdkIntAtLeast

/**
 * Created by sca_tl at 2023/7/31 17:17
 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
fun isGTESdk28() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun isGTESdk31() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isGTESdk29() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
fun isGTESdk35() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM

fun ComponentActivity.setSecureFlag(enable: Boolean) {
    if (enable) {
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE,)
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}

fun getVersionName(context: Context): String {
    try {
        return context.packageManager?.getPackageInfo(context.packageName, 0)?.versionName ?: ""
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

fun goToAppNotificationChannelSetting(context: Context?, channelId: String) {
    runCatching {
        val intent = Intent().apply {
            action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
            putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
        }
        context?.startActivity(intent)
    }
}

fun goToAppNotificationSetting(context: Context?) {
    runCatching {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
            putExtra(Settings.EXTRA_CHANNEL_ID, context?.applicationInfo?.uid)
        }
        context?.startActivity(intent)
    }
}