package com.amr.app

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditorViewModel : ViewModel() {
    // Счетчик для создания уникальных имен для новых файлов
    private var newFileCounter = 1

    // Поток, который хранит список всех открытых вкладок. UI будет на него подписан.
    private val _tabs = MutableStateFlow<List<FileTab>>(emptyList())
    val tabs = _tabs.asStateFlow()

    // Поток, который хранит индекс активной вкладки
    private val _activeTabIndex = MutableStateFlow(0)
    val activeTabIndex = _activeTabIndex.asStateFlow()

    // Функция для создания новой вкладки
    fun createNewTab() {
        val newTab = FileTab(
            id = (_tabs.value.maxOfOrNull { it.id } ?: 0) + 1,
            name = "Untitled-${newFileCounter++}",
            content = "" // Новый файл всегда пустой
        )
        _tabs.update { currentTabs -> currentTabs + newTab }
        // Делаем новую вкладку активной
        _activeTabIndex.value = _tabs.value.lastIndex
    }

    // Функция для добавления вкладки с открытым файлом
    fun openFileTab(fileName: String, fileContent: String) {
        val newTab = FileTab(
            id = (_tabs.value.maxOfOrNull { it.id } ?: 0) + 1,
            name = fileName,
            content = fileContent
        )
        _tabs.update { currentTabs -> currentTabs + newTab }
        _activeTabIndex.value = _tabs.value.lastIndex
    }

    // Функция для смены активной вкладки
    fun onTabSelected(index: Int) {
        _activeTabIndex.value = index
    }

    // Функция для обновления содержимого активной вкладки
    fun onContentChanged(newContent: String) {
        if (_tabs.value.isNotEmpty()) {
            val activeIndex = _activeTabIndex.value
            _tabs.update { currentTabs ->
                currentTabs.toMutableList().also {
                    it[activeIndex] = it[activeIndex].copy(content = newContent)
                }
            }
        }
    }
}
