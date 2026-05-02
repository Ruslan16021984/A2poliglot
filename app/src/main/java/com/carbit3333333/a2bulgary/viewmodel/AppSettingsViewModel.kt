package com.carbit3333333.a2bulgary.viewmodel

import android.app.Application
import android.content.pm.ApplicationInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carbit3333333.a2bulgary.data.billing.PurchaseAccessStore
import com.carbit3333333.a2bulgary.data.settings.AppLanguage
import com.carbit3333333.a2bulgary.data.settings.AppSettingsStore
import com.carbit3333333.a2bulgary.data.settings.AppThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppSettingsUiState(
    val themeMode: AppThemeMode = AppThemeMode.System,
    val language: AppLanguage = AppLanguage.System,
    val hasFullCourseAccess: Boolean = false,
    val isPurchaseFlowAvailable: Boolean = false,
    val showDeveloperActions: Boolean = false,
)

class AppSettingsViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val isDebugBuild =
        (application.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    private val settingsStore = AppSettingsStore(application)
    private val purchaseAccessStore = PurchaseAccessStore(application)

    val uiState: StateFlow<AppSettingsUiState> =
        combine(
            settingsStore.themeModeFlow,
            settingsStore.languageFlow,
            purchaseAccessStore.hasFullCourseAccessFlow,
        ) { themeMode, language, hasFullCourseAccess ->
            AppSettingsUiState(
                themeMode = themeMode,
                language = language,
                hasFullCourseAccess = hasFullCourseAccess,
                isPurchaseFlowAvailable = false,
                showDeveloperActions = isDebugBuild,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettingsUiState(),
        )

    fun updateThemeMode(themeMode: AppThemeMode) {
        viewModelScope.launch {
            settingsStore.saveThemeMode(themeMode)
        }
    }

    fun updateLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsStore.saveLanguage(language)
        }
    }

    suspend fun launchFullCoursePurchase(): Boolean {
        return false
    }

    fun restorePurchases() = Unit

    fun revokeFullCourseAccess() {
        if (!isDebugBuild) return
        viewModelScope.launch {
            purchaseAccessStore.setFullCourseAccess(false)
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AppSettingsViewModel(application) as T
                }
            }
    }
}
