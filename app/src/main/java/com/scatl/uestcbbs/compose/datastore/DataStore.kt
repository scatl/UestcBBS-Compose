package com.scatl.uestcbbs.compose.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.scatl.uestcbbs.compose.App
import com.scatl.uestcbbs.compose.manager.LanguageManager
import com.scatl.uestcbbs.compose.manager.ThemeManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by sca_tl at 2024/6/11 19:17:40
 */

val Context.settingsDataStore by preferencesDataStore(name = "settings")
val Context.bizDataStore by preferencesDataStore(name = "biz")

object DataStore {
    var interfaceLanguage by DataStoreDelegate(App.context.settingsDataStore, stringPreferencesKey("interfaceLanguage"), LanguageManager.InterfaceLanguage.FOLLOW_SYSTEM.value)
    var dynamicTheme by DataStoreDelegate(App.context.settingsDataStore, booleanPreferencesKey("dynamicTheme"), true)
    var customTheme by DataStoreDelegate(App.context.settingsDataStore, stringPreferencesKey("customTheme"), "")
    var customThemeScheme by DataStoreDelegate(App.context.settingsDataStore, stringPreferencesKey("customThemeScheme"), ThemeManager.ThemeScheme.FIDELITY.value)
    var dayNightMode by DataStoreDelegate(App.context.settingsDataStore, intPreferencesKey("dayNightMode"), ThemeManager.DayNightMode.FOLLOW_SYSTEM.value)
    var hideDailyPic by DataStoreDelegate(App.context.settingsDataStore, booleanPreferencesKey("hideDailyPic"), false)
    var ignoreSSL by DataStoreDelegate(App.context.settingsDataStore, booleanPreferencesKey("ignoreSSL"), false)
    var downloadFolderUri by DataStoreDelegate(App.context.settingsDataStore, stringPreferencesKey("downloadFolderUri"), "")
    var videoAutoPlay by DataStoreDelegate(App.context.settingsDataStore, booleanPreferencesKey("videoAutoPlay"), true)
    var autoDayQuestion by DataStoreDelegate(App.context.settingsDataStore, booleanPreferencesKey("autoDayQuestion"), true)
    var keyboardHeight by DataStoreDelegate(App.context.settingsDataStore, floatPreferencesKey("keyboardHeight"), 250f)

    var tipShowedId by DataStoreDelegate(App.context.bizDataStore, stringSetPreferencesKey("tipShowedId"), mutableSetOf())
    var legacyForumHash by DataStoreDelegate(App.context.bizDataStore, stringPreferencesKey("legacyForumHash"), "")
}

class DataStoreDelegate<T>(
    private val dataStore: androidx.datastore.core.DataStore<Preferences>,
    private val key: Preferences.Key<T>,
    private val defaultValue: T
) : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val preferencesFlow = dataStore.data
        return runBlocking {
            preferencesFlow
                .map { it[key] ?: defaultValue }
                .first()
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }
}