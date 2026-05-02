package com.carbit3333333.a2bulgary.data.billing

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.purchaseAccessDataStore by preferencesDataStore(name = "purchase_access")

class PurchaseAccessStore(
    context: Context,
) {
    private val appContext = context.applicationContext

    val hasFullCourseAccessFlow: Flow<Boolean> =
        appContext.purchaseAccessDataStore.data.map { preferences ->
            preferences[Keys.HAS_FULL_COURSE_ACCESS] ?: false
        }

    suspend fun setFullCourseAccess(enabled: Boolean) {
        appContext.purchaseAccessDataStore.edit { preferences ->
            preferences[Keys.HAS_FULL_COURSE_ACCESS] = enabled
        }
    }

    private object Keys {
        val HAS_FULL_COURSE_ACCESS = booleanPreferencesKey("has_full_course_access")
    }
}
