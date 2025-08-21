package com.amr.app

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class EditorViewModel : ViewModel() {
    private var newFileCounter = 1

    private val _tabs = MutableStateFlow<List<FileTab>>(emptyList())
    val tabs = _tabs.asStateFlow()
    private val _activeTabIndex = MutableStateFlow(0)
    val activeTabIndex = _activeTabIndex.asStateFlow()

    private val _fileTree = MutableStateFlow<FileTreeNode?>(null)
    val fileTree = _fileTree.asStateFlow()

    init {
        // При старте сразу показываем корневую папку устройства
        loadInitialDirectory()
    }

    private fun loadInitialDirectory() {
        val rootDir = Environment.getExternalStorageDirectory()
        buildFileTreeFromPath(rootDir)
    }

    // --- НОВАЯ БЫСТРАЯ ЛОГИКА ---
    private fun buildFileTreeFromPath(targetFile: File) {
        viewModelScope.launch {
            val rootNode = withContext(Dispatchers.IO) {
                buildNode(targetFile)
            }
            _fileTree.value = rootNode
        }
    }

    private fun buildNode(file: File, currentDepth: Int = 0): FileTreeNode {
        val children = if (file.isDirectory) {
            // ЗАГРУЖАЕМ ТОЛЬКО ТЕКУЩИЙ УРОВЕНЬ! (Lazy loading)
            file.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
                ?.map {
                    // Для дочерних элементов не загружаем их детей сразу
                    FileTreeNode(
                        path = it.path,
                        name = it.name,
                        isDirectory = it.isDirectory,
                        children = emptyList(), // Дети будут загружены при нажатии
                        depth = currentDepth + 1,
                        isExpanded = false
                    )
                } ?: emptyList()
        } else {
            emptyList()
        }
        return FileTreeNode(
            path = file.path,
            name = file.name,
            isDirectory = file.isDirectory,
            children = children,
            depth = currentDepth,
            isExpanded = true // Корневой узел всегда "раскрыт"
        )
    }

    fun toggleNodeExpansion(nodeToToggle: FileTreeNode) {
        viewModelScope.launch {
            val newRoot = withContext(Dispatchers.IO) {
                updateNodeExpansion(_fileTree.value!!, nodeToToggle.path)
            }
            _fileTree.value = newRoot
        }
    }

    private fun updateNodeExpansion(currentNode: FileTreeNode, targetPath: String): FileTreeNode {
        if (currentNode.path == targetPath) {
            // Если мы нашли узел, который нужно раскрыть/свернуть
            return if (currentNode.isExpanded) {
                // Сворачиваем: просто удаляем детей
                currentNode.copy(isExpanded = false, children = emptyList())
            } else {
                // Раскрываем: загружаем детей с диска
                val file = File(currentNode.path)
                val children = file.listFiles()
                    ?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
                    ?.map {
                        FileTreeNode(
                            path = it.path, name = it.name, isDirectory = it.isDirectory,
                            children = emptyList(), depth = currentNode.depth + 1, isExpanded = false
                        )
                    } ?: emptyList()
                currentNode.copy(isExpanded = true, children = children)
            }
        }

        // Рекурсивно ищем узел в дочерних элементах
        return currentNode.copy(
            children = currentNode.children.map { child ->
                updateNodeExpansion(child, targetPath)
            }
        )
    }

    // --- Функции для вкладок ---
    fun createNewTab() {
        val newTab = FileTab(name = "new $newFileCounter.txt", content = "")
        _tabs.update { it + newTab }
        _activeTabIndex.value = _tabs.value.lastIndex
        newFileCounter++
    }

    fun openFile(file: File) {
        viewModelScope.launch {
            val content = withContext(Dispatchers.IO) {
                try { file.readText() } catch (e: Exception) { e.message ?: "Error reading file" }
            }
            val existingTabIndex = _tabs.value.indexOfFirst { it.path == file.path }
            if (existingTabIndex != -1) {
                _activeTabIndex.value = existingTabIndex
            } else {
                val newTab = FileTab(path = file.path, name = file.name, content = content)
                _tabs.update { it + newTab }
                _activeTabIndex.value = _tabs.value.lastIndex
            }
        }
    }

    fun onTabSelected(index: Int) {
        _activeTabIndex.value = index
    }

    fun onContentChanged(newContent: String) {
        _tabs.update { currentTabs ->
            currentTabs.mapIndexed { index, tab ->
                if (index == _activeTabIndex.value) {
                    tab.copy(content = newContent)
                } else {
                    tab
                }
            }
        }
    }
}
