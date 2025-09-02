package com.amr.app.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TERMINAL_PREFS_NAME = "terminal_prefs"
private val Context.dataStore by preferencesDataStore(name = TERMINAL_PREFS_NAME)

object TerminalThemePrefs {
    private val KEY_IS_DARK = booleanPreferencesKey("terminal_is_dark")

    fun isDarkTheme(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[KEY_IS_DARK] ?: true } // по умолчанию тёмная

    suspend fun setDarkTheme(context: Context, isDark: Boolean) {
        context.dataStore.edit { it[KEY_IS_DARK] = isDark }
    }
}