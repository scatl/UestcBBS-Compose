package com.scatl.uestcbbs.compose.manager

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import java.util.Locale

/**
 * Created by sca_tl at 2024/6/27 10:44:27
 */
object LanguageManager {

    enum class InterfaceLanguage(val value: String) {
        FOLLOW_SYSTEM("system"),
        CHINESE("zh-CN"),
        ENGLISH("en-US")
    }

    fun updateInterfaceLanguage(value: InterfaceLanguage?, context: Context) {
        val config = Configuration(context.resources.configuration)

        val localeList = ConfigurationCompat.getLocales(Resources.getSystem().configuration)
        val system = localeList.get(0) ?: Locale.getDefault()

        val locale = when (value) {
            InterfaceLanguage.CHINESE -> Locale.CHINESE
            InterfaceLanguage.ENGLISH -> Locale.ENGLISH
            else -> system
        }

        Locale.setDefault(locale)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}