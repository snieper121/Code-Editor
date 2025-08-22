package com.amr.app

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class EditorViewModel : ViewModel() {
    private var newFileCounter = 1

    private val _tabs = MutableStateFlow<List<FileTab>>(emptyList())
    val tabs = _tabs.asStateFlow()
    private val _activeTabIndex = MutableStateFlow(0)
    val activeTabIndex = _activeTabIndex.asStateFlow()

    private val _fileTree = MutableStateFlow<FileTreeNode?>(null)
    val fileTree = _fileTree.asStateFlow()
    
    fun buildFileTreeFromUri(context: Context, rootUri: Uri) {
        viewModelScope.launch {
            _fileTree.value = null
            val rootDoc = withContext(Dispatchers.IO) {
                try { DocumentFile.fromTreeUri(context, rootUri) } catch (_: Exception) { null }
            }
            val rootNode = rootDoc?.let {
                FileTreeNode(
                    name = it.name ?: "root",
                    uri = it.uri,
                    isDirectory = it.isDirectory,
                    children = null,               // детей пока не грузим
                    depth = 0,
                    isExpanded = true,             // корень раскрыт
                    isLoading = true               // и сразу загружаем детей
                )
            }
            _fileTree.value = rootNode
            if (rootDoc != null && rootNode != null) {
                val children = withContext(Dispatchers.IO) { listChildren(rootDoc, 1) }
                _fileTree.value = _fileTree.value?.copy(children = children, isLoading = false)
            }
        }
    }
    
    private suspend fun listChildren(documentFile: DocumentFile, depth: Int): List<FileTreeNode> {
        return documentFile.listFiles()
            .sortedWith(compareBy<DocumentFile>({ !it.isDirectory }, { it.name ?: "" }))
            .map { child ->
                FileTreeNode(
                    name = child.name ?: "unknown",
                    uri = child.uri,
                    isDirectory = child.isDirectory,
                    children = if (child.isDirectory) null else emptyList(), // у папок дети будут лениво
                    depth = depth,
                    isExpanded = false,
                    isLoading = false
                )
            }
    }
    
    private fun updateNode(
        current: FileTreeNode,
        targetId: String, // используем ID вместо URI
        transform: (FileTreeNode) -> FileTreeNode
    ): FileTreeNode {
        if (current.id == targetId) return transform(current)
        val newChildren = current.children?.map { updateNode(it, targetId, transform) }
        return current.copy(children = newChildren)
    }
    
    fun toggleNodeExpansion(context: Context, nodeToToggle: FileTreeNode) {
        val expanding = !nodeToToggle.isExpanded
        _fileTree.value?.let { root ->
            // Сначала обновляем состояние разворачивания
            _fileTree.value = updateNode(root, nodeToToggle.id) { it.copy(isExpanded = expanding) }
            // Если разворачиваем папку и у неё нет детей
            if (expanding && nodeToToggle.isDirectory && nodeToToggle.children == null) {
                viewModelScope.launch {
                    try {
                        // Устанавливаем состояние загрузки
                        _fileTree.value = _fileTree.value?.let { r ->
                            updateNode(r, nodeToToggle.id) { it.copy(isLoading = true) }
                        }
                        
                        // Загружаем детей
                        val children = withContext(Dispatchers.IO) {
                            val doc = DocumentFile.fromTreeUri(context, nodeToToggle.uri)
                            if (doc != null && doc.isDirectory) {
                                listChildren(doc, nodeToToggle.depth + 1)
                            } else {
                                emptyList()
                            }
                        }
                        
                        // Обновляем с детьми и убираем загрузку
                        _fileTree.value = _fileTree.value?.let { r ->
                            updateNode(r, nodeToToggle.id) { 
                                it.copy(children = children, isLoading = false) 
                            }
                        }
                    } catch (e: Exception) {
                        // В случае ошибки убираем загрузку и оставляем пустой список
                        _fileTree.value = _fileTree.value?.let { r ->
                            updateNode(r, nodeToToggle.id) { 
                                it.copy(children = emptyList(), isLoading = false) 
                            }
                        }
                    }
                }
            }
        }
    }

    fun createNewTab() {
        val newTab = FileTab(name = "new $newFileCounter.txt", content = "")
        _tabs.update { it + newTab }
        _activeTabIndex.value = _tabs.value.lastIndex
        newFileCounter++
    }

    fun openFileTab(context: Context, fileUri: Uri, fileName: String) {
        viewModelScope.launch {
            val content = withContext(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(fileUri)?.bufferedReader()?.readText()
                } catch (e: Exception) {
                    e.message ?: "Error reading file"
                }
            }
            if (content != null) {
                val existingTabIndex = _tabs.value.indexOfFirst { it.uri == fileUri }
                if (existingTabIndex != -1) {
                    _activeTabIndex.value = existingTabIndex
                } else {
                    val newTab = FileTab(uri = fileUri, name = fileName, content = content)
                    _tabs.update { it + newTab }
                    _activeTabIndex.value = _tabs.value.lastIndex
                }
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
    
    fun closeTab(index: Int) {
        val current = _tabs.value
        if (index !in current.indices) return
        val newList = current.toMutableList().also { it.removeAt(index) }
        _tabs.value = newList
        val oldActive = _activeTabIndex.value
        _activeTabIndex.value = when {
            newList.isEmpty() -> 0
            index < oldActive -> oldActive - 1
            index == oldActive -> minOf(index, newList.lastIndex)
            else -> oldActive
        }
    }
}
