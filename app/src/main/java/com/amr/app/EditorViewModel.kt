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
            val rootNode = withContext(Dispatchers.IO) {
                try {
                    val rootDocument = DocumentFile.fromTreeUri(context, rootUri)
                    rootDocument?.let { buildNode(it) }
                } catch (_: Exception) {
                    null
                }
            }
            _fileTree.value = rootNode?.copy(isExpanded = true)  // авто-раскрыть корень
        }
    }
    
    private fun buildNode(documentFile: DocumentFile, currentDepth: Int = 0): FileTreeNode {
        val children = if (documentFile.isDirectory) {
            documentFile.listFiles()
                .sortedWith(compareBy<DocumentFile>({ !it.isDirectory }, { it.name ?: "" })) // null-safe
                .mapNotNull { if (it.canRead()) buildNode(it, currentDepth + 1) else null }
        } else {
            emptyList()
        }
        return FileTreeNode(
            name = documentFile.name ?: "unknown",
            uri = documentFile.uri,
            isDirectory = documentFile.isDirectory,
            children = children,
            depth = currentDepth
        )
    }

    fun toggleNodeExpansion(nodeToToggle: FileTreeNode) {
        _fileTree.value?.let { root ->
            val newRoot = updateNodeExpansion(root, nodeToToggle.uri, !nodeToToggle.isExpanded)
            _fileTree.value = newRoot
        }
    }

    private fun updateNodeExpansion(currentNode: FileTreeNode, targetUri: Uri, isExpanded: Boolean): FileTreeNode {
        if (currentNode.uri == targetUri) {
            return currentNode.copy(isExpanded = isExpanded)
        }
        return currentNode.copy(
            children = currentNode.children.map { child ->
                updateNodeExpansion(child, targetUri, isExpanded)
            }
        )
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
}
