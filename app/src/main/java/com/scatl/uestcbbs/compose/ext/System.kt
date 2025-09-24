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
import androidx.core.app.ActivityCompat
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.widget.image.picker.permissions

/**
 * Created by sca_tl at 2023/7/31 17:17
 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
fun isGTESdk28() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isGTESdk29() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
fun isGTESdk30() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun isGTESdk31() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun isGTESdk33() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
fun isGTESdk35() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM

fun ComponentActivity.setSecureFlag(enable: Boolean) {
    if (enable) {
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE,)
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}

fun Context?.getVersionName(): String {
    return try {
        return this?.packageManager?.getPackageInfo(packageName, 0)?.versionName ?: ""
    } catch (e: Exception) {
        XLog.e(e)
        ""
    }
}

fun Context?.goToAppNotificationChannelSetting(channelId: String) {
    this?.let {
        runCatching {
            val intent = Intent().apply {
                action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
            }
            startActivity(intent)
        }
    }
}

fun Context?.goToAppNotificationSetting() {
    this?.let {
        runCatching {
            val intent = Intent().apply {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, applicationInfo?.uid)
            }
            startActivity(intent)
        }
    }
}

fun Context.hasPermission(permissions: Array<String>): Boolean {
    return permissions.all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}