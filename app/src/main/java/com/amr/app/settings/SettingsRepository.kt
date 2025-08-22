package com.amr.app.settings

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

private val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val EDITOR_FONT_SIZE_SP = floatPreferencesKey("editor_font_size_sp")
    val EDITOR_FONT_SIZE_PX = intPreferencesKey("editor_font_size_px")
    val EDITOR_SCALE = floatPreferencesKey("editor_scale")
    val EDITOR_LINE_NUMBERS = booleanPreferencesKey("editor_line_numbers")
    val EDITOR_HIGHLIGHT_LINE = booleanPreferencesKey("editor_highlight_line")
    val EDITOR_TOUCH_DELAY = intPreferencesKey("editor_touch_delay") // новая настройка
}

class SettingsRepository(private val context: Context) {
    val editorFontSizePx: Flow<Int> =
        context.dataStore.data.map { it[SettingsKeys.EDITOR_FONT_SIZE_PX] ?: 16 }
    
    suspend fun setEditorFontSizePx(value: Int) {
        context.dataStore.edit { it[SettingsKeys.EDITOR_FONT_SIZE_PX] = value }
    }

    val editorFontSizeSp: Flow<Float> =
        context.dataStore.data.map { it[SettingsKeys.EDITOR_FONT_SIZE_SP] ?: 14f }

    val editorScale: Flow<Float> =
        context.dataStore.data.map { it[SettingsKeys.EDITOR_SCALE] ?: 1.0f }

    val editorLineNumbers: Flow<Boolean> =
        context.dataStore.data.map { it[SettingsKeys.EDITOR_LINE_NUMBERS] ?: true }

    val editorHighlightLine: Flow<Boolean> =
        context.dataStore.data.map { it[SettingsKeys.EDITOR_HIGHLIGHT_LINE] ?: true }

    val editorTouchDelay: Flow<Int> =
        context.dataStore.data.map { it[SettingsKeys.EDITOR_TOUCH_DELAY] ?: 500 }
    
    suspend fun setEditorTouchDelay(value: Int) {
        context.dataStore.edit { it[SettingsKeys.EDITOR_TOUCH_DELAY] = value }
    }

    suspend fun setEditorFontSizeSp(value: Float) {
        context.dataStore.edit { it[SettingsKeys.EDITOR_FONT_SIZE_SP] = value }
    }

    suspend fun setEditorScale(value: Float) {
        context.dataStore.edit { it[SettingsKeys.EDITOR_SCALE] = value }
    }

    suspend fun setEditorLineNumbers(enabled: Boolean) {
        context.dataStore.edit { it[SettingsKeys.EDITOR_LINE_NUMBERS] = enabled }
    }

    suspend fun setEditorHighlightLine(enabled: Boolean) {
        context.dataStore.edit { it[SettingsKeys.EDITOR_HIGHLIGHT_LINE] = enabled }
    }
}