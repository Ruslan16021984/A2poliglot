package com.carbit3333333.a2bulgary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.carbit3333333.a2bulgary.data.settings.AppLanguage
import com.carbit3333333.a2bulgary.data.settings.AppSettingsStore
import com.carbit3333333.a2bulgary.data.settings.AppThemeMode
import com.carbit3333333.a2bulgary.navigation.AppNavGraph
import com.carbit3333333.a2bulgary.ui.theme.A2BulgaryTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appSettingsStore = AppSettingsStore(applicationContext)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appSettingsStore.languageFlow
                    .distinctUntilChanged()
                    .collect { language ->
                        applyLanguageIfNeeded(language)
                    }
            }
        }

        setContent {
            val settingsStore = remember { appSettingsStore }
            val themeMode by settingsStore.themeModeFlow.collectAsState(initial = AppThemeMode.System)

            A2BulgaryTheme(appThemeMode = themeMode) {
                AppNavGraph()
            }
        }
    }

    private fun applyLanguageIfNeeded(appLanguage: AppLanguage) {
        val targetLocales = when (appLanguage) {
            AppLanguage.System -> LocaleListCompat.getEmptyLocaleList()
            AppLanguage.Russian -> LocaleListCompat.forLanguageTags(AppLanguage.Russian.tag)
            AppLanguage.Ukrainian -> LocaleListCompat.forLanguageTags(AppLanguage.Ukrainian.tag)
        }

        if (AppCompatDelegate.getApplicationLocales() != targetLocales) {
            AppCompatDelegate.setApplicationLocales(targetLocales)
        }
    }
}
