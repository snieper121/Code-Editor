package com.amr.app

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.update
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

    // --- НОВАЯ ЛОГИКА: Принимаем Uri, конвертируем в Path и строим дерево ---
    fun loadProjectFromUri(context: Context, rootUri: Uri) {
        viewModelScope.launch {
            val rootPath = withContext(Dispatchers.IO) {
                UriPathHelper.getPath(context, rootUri)
            }
            if (rootPath != null) {
                val rootFile = File(rootPath)
                val rootNode = withContext(Dispatchers.IO) {
                    buildNode(rootFile)
                }
                _fileTree.value = rootNode
            } else {
                // Обработка ошибки, если путь не удалось получить
            }
        }
    }

    private fun buildNode(file: File, currentDepth: Int = 0): FileTreeNode {
        val children = if (file.isDirectory) {
            file.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
                ?.map {
                    FileTreeNode(
                        path = it.path, name = it.name, isDirectory = it.isDirectory,
                        children = emptyList(), depth = currentDepth + 1, isExpanded = false
                    )
                } ?: emptyList()
        } else {
            emptyList()
        }
        return FileTreeNode(
            path = file.path, name = file.name, isDirectory = file.isDirectory,
            children = children, depth = currentDepth, isExpanded = true
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
            return if (currentNode.isExpanded) {
                currentNode.copy(isExpanded = false, children = emptyList())
            } else {
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
