package com.carbit3333333.a2bulgary.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appSettingsDataStore by preferencesDataStore(name = "app_settings")

enum class AppThemeMode {
    System,
    Light,
    Dark,
}

enum class AppLanguage(val tag: String) {
    System(""),
    Russian("ru"),
    Ukrainian("uk"),
}

class AppSettingsStore(
    context: Context,
) {
    private val appContext = context.applicationContext

    val themeModeFlow: Flow<AppThemeMode> =
        appContext.appSettingsDataStore.data.map { preferences ->
            preferences[Keys.THEME_MODE]
                ?.let(::parseThemeMode)
                ?: AppThemeMode.System
        }

    val languageFlow: Flow<AppLanguage> =
        appContext.appSettingsDataStore.data.map { preferences ->
            preferences[Keys.APP_LANGUAGE]
                ?.let(::parseLanguage)
                ?: AppLanguage.System
        }

    suspend fun saveThemeMode(themeMode: AppThemeMode) {
        appContext.appSettingsDataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun saveLanguage(language: AppLanguage) {
        appContext.appSettingsDataStore.edit { preferences ->
            preferences[Keys.APP_LANGUAGE] = language.name
        }
    }

    private fun parseThemeMode(rawValue: String): AppThemeMode {
        return AppThemeMode.entries.firstOrNull { it.name == rawValue } ?: AppThemeMode.System
    }

    private fun parseLanguage(rawValue: String): AppLanguage {
        return AppLanguage.entries.firstOrNull { it.name == rawValue } ?: AppLanguage.System
    }

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
    }
}
