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
import com.amr.app.file.FileManager

class EditorViewModel : ViewModel() {
    private var newFileCounter = 1
    private var fileManager: FileManager? = null

    private val _tabs = MutableStateFlow<List<FileTab>>(emptyList())
    val tabs = _tabs.asStateFlow()
    private val _activeTabIndex = MutableStateFlow(0)
    val activeTabIndex = _activeTabIndex.asStateFlow()

    private val _fileTree = MutableStateFlow<FileTreeNode?>(null)
    val fileTree = _fileTree.asStateFlow()
    
    fun buildFileTreeFromUri(context: Context, rootUri: Uri) {
        fileManager = FileManager(context)
        viewModelScope.launch {
            _fileTree.value = null
            val rootDoc = fileManager?.getRootDocument(rootUri)
            val rootNode = rootDoc?.let {
                FileTreeNode(
                    name = it.name ?: "root",
                    uri = it.uri,
                    documentFile = it,
                    isDirectory = it.isDirectory,
                    children = null,
                    depth = 0,
                    isExpanded = true,
                    isLoading = true
                )
            }
            _fileTree.value = rootNode
            
            if (rootDoc != null && rootNode != null) {
                val children = loadChildren(rootDoc, 1)
                _fileTree.value = _fileTree.value?.copy(children = children, isLoading = false)
            }
        }
    }
    
    private suspend fun loadChildren(document: DocumentFile, depth: Int): List<FileTreeNode> {
        val files = fileManager?.listChildren(document) ?: emptyList()
        return files.map { child ->
            FileTreeNode(
                name = child.name ?: "unknown",
                uri = child.uri,
                documentFile = child,
                isDirectory = child.isDirectory,
                children = if (child.isDirectory) null else emptyList(),
                depth = depth,
                isExpanded = false,
                isLoading = false
            )
        }
    }
    
    private fun updateNode(
        current: FileTreeNode,
        targetId: String,
        transform: (FileTreeNode) -> FileTreeNode
    ): FileTreeNode {
        if (current.id == targetId) return transform(current)
        val newChildren = current.children?.map { updateNode(it, targetId, transform) }
        return current.copy(children = newChildren)
    }
    
    fun toggleNodeExpansion(context: Context, nodeToToggle: FileTreeNode) {
        val expanding = !nodeToToggle.isExpanded
        _fileTree.value?.let { root ->
            _fileTree.value = updateNode(root, nodeToToggle.id) { it.copy(isExpanded = expanding) }

            if (expanding && nodeToToggle.isDirectory && nodeToToggle.children == null) {
                viewModelScope.launch {
                    try {
                        _fileTree.value = _fileTree.value?.let { r ->
                            updateNode(r, nodeToToggle.id) { it.copy(isLoading = true) }
                        }
                        
                        // Используем сохранённый DocumentFile вместо создания нового
                        val children = nodeToToggle.documentFile?.let { doc ->
                            loadChildren(doc, nodeToToggle.depth + 1)
                        } ?: emptyList()
                        
                        _fileTree.value = _fileTree.value?.let { r ->
                            updateNode(r, nodeToToggle.id) { 
                                it.copy(children = children, isLoading = false) 
                            }
                        }
                    } catch (e: Exception) {
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
            val content = fileManager?.readFileContent(fileUri)
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