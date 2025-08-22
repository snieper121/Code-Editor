package com.amr.app.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SettingsRepository(app)

    val editorFontSizeSp = repo.editorFontSizeSp.stateIn(viewModelScope, SharingStarted.Eagerly, 14f)
    val editorScale = repo.editorScale.stateIn(viewModelScope, SharingStarted.Eagerly, 1.0f)
    val editorLineNumbers = repo.editorLineNumbers.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val editorHighlightLine = repo.editorHighlightLine.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val editorFontSizePx = repo.editorFontSizePx.stateIn(viewModelScope, SharingStarted.Eagerly, 16)
    val editorTouchDelay = repo.editorTouchDelay.stateIn(viewModelScope, SharingStarted.Eagerly, 500)

    fun setEditorTouchDelay(value: Int) = viewModelScope.launch { repo.setEditorTouchDelay(value) }
    fun setEditorFontSizePx(value: Int) = viewModelScope.launch { repo.setEditorFontSizePx(value) }
    fun setEditorFontSizeSp(value: Float) = viewModelScope.launch { repo.setEditorFontSizeSp(value) }
    fun setEditorScale(value: Float) = viewModelScope.launch { repo.setEditorScale(value) }
    fun setEditorLineNumbers(enabled: Boolean) = viewModelScope.launch { repo.setEditorLineNumbers(enabled) }
    fun setEditorHighlightLine(enabled: Boolean) = viewModelScope.launch { repo.setEditorHighlightLine(enabled) }
}