package com.carbit3333333.a2bulgary.ui.settings

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carbit3333333.a2bulgary.R
import com.carbit3333333.a2bulgary.data.settings.AppLanguage
import com.carbit3333333.a2bulgary.data.settings.AppThemeMode
import com.carbit3333333.a2bulgary.ui.dictionary.rememberDictionaryPalette
import com.carbit3333333.a2bulgary.ui.theme.A2BulgaryTheme
import com.carbit3333333.a2bulgary.viewmodel.AppSettingsUiState
import com.carbit3333333.a2bulgary.viewmodel.AppSettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: AppSettingsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    SettingsScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onThemeModeSelect = viewModel::updateThemeMode,
        onLanguageSelect = viewModel::updateLanguage,
        onBuyFullCourseClick = {
            context.findActivity() ?: return@SettingsScreenContent false
            viewModel.launchFullCoursePurchase()
        },
        onRestorePurchasesClick = viewModel::restorePurchases,
        onRevokeFullCourseAccessClick = viewModel::revokeFullCourseAccess,
    )
}

@Composable
fun SettingsScreenContent(
    uiState: AppSettingsUiState,
    onBackClick: () -> Unit,
    onThemeModeSelect: (AppThemeMode) -> Unit,
    onLanguageSelect: (AppLanguage) -> Unit,
    onBuyFullCourseClick: suspend () -> Boolean,
    onRestorePurchasesClick: () -> Unit,
    onRevokeFullCourseAccessClick: () -> Unit,
) {
    val palette = rememberDictionaryPalette()
    var showPurchaseInfoDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = palette.pageBackground,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.clip(CircleShape),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = palette.title,
                        )
                    }

                    Column {
                        Text(
                            text = stringResource(R.string.settings_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.title,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.settings_summary),
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.body,
                        )
                    }
                }
            }

            item {
                SettingsSectionCard(
                    title = stringResource(R.string.settings_theme_title),
                    description = stringResource(R.string.settings_theme_description),
                    surfaceColor = palette.surface,
                    borderColor = palette.border,
                    titleColor = palette.title,
                    bodyColor = palette.body,
                ) {
                    SettingOptionRow(
                        title = stringResource(R.string.settings_theme_system),
                        selected = uiState.themeMode == AppThemeMode.System,
                        onClick = { onThemeModeSelect(AppThemeMode.System) },
                        titleColor = palette.title,
                    )
                    SettingOptionRow(
                        title = stringResource(R.string.settings_theme_light),
                        selected = uiState.themeMode == AppThemeMode.Light,
                        onClick = { onThemeModeSelect(AppThemeMode.Light) },
                        titleColor = palette.title,
                    )
                    SettingOptionRow(
                        title = stringResource(R.string.settings_theme_dark),
                        selected = uiState.themeMode == AppThemeMode.Dark,
                        onClick = { onThemeModeSelect(AppThemeMode.Dark) },
                        titleColor = palette.title,
                    )
                }
            }

            item {
                SettingsSectionCard(
                    title = stringResource(R.string.settings_language_title),
                    description = stringResource(R.string.settings_language_description),
                    surfaceColor = palette.surface,
                    borderColor = palette.border,
                    titleColor = palette.title,
                    bodyColor = palette.body,
                ) {
                    SettingOptionRow(
                        title = stringResource(R.string.settings_language_system),
                        selected = uiState.language == AppLanguage.System,
                        onClick = { onLanguageSelect(AppLanguage.System) },
                        titleColor = palette.title,
                    )
                    SettingOptionRow(
                        title = stringResource(R.string.settings_language_russian),
                        selected = uiState.language == AppLanguage.Russian,
                        onClick = { onLanguageSelect(AppLanguage.Russian) },
                        titleColor = palette.title,
                    )
                    SettingOptionRow(
                        title = stringResource(R.string.settings_language_ukrainian),
                        selected = uiState.language == AppLanguage.Ukrainian,
                        onClick = { onLanguageSelect(AppLanguage.Ukrainian) },
                        titleColor = palette.title,
                    )
                }
            }

            item {
                SettingsSectionCard(
                    title = stringResource(R.string.settings_full_access_title),
                    description = if (uiState.hasFullCourseAccess) {
                        stringResource(R.string.settings_full_access_active_description)
                    } else {
                        stringResource(R.string.settings_full_access_inactive_description)
                    },
                    surfaceColor = palette.surface,
                    borderColor = palette.border,
                    titleColor = palette.title,
                    bodyColor = palette.body,
                ) {
                    Text(
                        text = if (uiState.hasFullCourseAccess) {
                            stringResource(R.string.settings_full_access_status_active)
                        } else {
                            stringResource(R.string.settings_full_access_status_inactive)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = palette.title,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (uiState.isPurchaseFlowAvailable) {
                                    scope.launch {
                                        val launched = onBuyFullCourseClick()
                                        if (!launched) {
                                            showPurchaseInfoDialog = true
                                        }
                                    }
                                } else {
                                    showPurchaseInfoDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringResource(R.string.settings_full_access_buy))
                        }

                        OutlinedButton(
                            onClick = onRestorePurchasesClick,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringResource(R.string.settings_full_access_restore))
                        }
                    }

                    if (uiState.hasFullCourseAccess && uiState.showDeveloperActions) {
                        OutlinedButton(
                            onClick = onRevokeFullCourseAccessClick,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.settings_full_access_revoke_debug))
                        }
                    }
                }
            }
        }

        if (showPurchaseInfoDialog) {
            AlertDialog(
                onDismissRequest = { showPurchaseInfoDialog = false },
                confirmButton = {
                    TextButton(onClick = { showPurchaseInfoDialog = false }) {
                        Text(stringResource(R.string.common_ok))
                    }
                },
                title = {
                    Text(stringResource(R.string.settings_full_access_dialog_title))
                },
                text = {
                    Text(stringResource(R.string.settings_full_access_dialog_message))
                },
            )
        }
    }
}

private fun Context.findActivity(): android.app.Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is android.app.Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

@Composable
private fun SettingsSectionCard(
    title: String,
    description: String,
    surfaceColor: Color,
    borderColor: Color,
    titleColor: Color,
    bodyColor: Color,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.65f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = bodyColor,
                )
            }
            content()
        }
    }
}

@Composable
private fun SettingOptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    titleColor: Color,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor,
            )
            RadioButton(
                selected = selected,
                onClick = onClick,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    A2BulgaryTheme {
        SettingsScreenContent(
            uiState = AppSettingsUiState(),
            onBackClick = {},
            onThemeModeSelect = {},
            onLanguageSelect = {},
            onBuyFullCourseClick = { false },
            onRestorePurchasesClick = {},
            onRevokeFullCourseAccessClick = {},
        )
    }
}
