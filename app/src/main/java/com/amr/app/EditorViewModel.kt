package com.amr.app

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditorViewModel : ViewModel() {
    private var newFileCounter = 1

    // --- Состояния для вкладок (без изменений) ---
    private val _tabs = MutableStateFlow<List<FileTab>>(emptyList())
    val tabs = _tabs.asStateFlow()
    private val _activeTabIndex = MutableStateFlow(0)
    val activeTabIndex = _activeTabIndex.asStateFlow()

    // --- НОВЫЕ СОСТОЯНИЯ ДЛЯ ФАЙЛОВОГО ДЕРЕВА ---
    private val _fileTree = MutableStateFlow<FileTreeNode?>(null)
    val fileTree = _fileTree.asStateFlow()

    // --- Функции для вкладок (без изменений) ---
    fun createNewTab() { /* ... */ }
    fun openFileTab(fileName: String, fileContent: String) { /* ... */ }
    fun onTabSelected(index: Int) { /* ... */ }
    fun onContentChanged(newContent: String) { /* ... */ }

    // --- НОВЫЕ ФУНКЦИИ ДЛЯ ФАЙЛОВОГО ДЕРЕВА ---

    // Главная функция, которая строит дерево по выбранной папке
    fun buildFileTreeFromUri(context: Context, rootUri: Uri) {
        val rootDocument = DocumentFile.fromTreeUri(context, rootUri)
        _fileTree.value = rootDocument?.let { buildNode(it) }
    }

    // Рекурсивная функция для построения узлов дерева
    private fun buildNode(documentFile: DocumentFile, currentDepth: Int = 0): FileTreeNode {
        val children = if (documentFile.isDirectory) {
            documentFile.listFiles()
                .sortedWith(compareBy({ !it.isDirectory }, { it.name })) // Папки сначала, потом файлы
                .map { buildNode(it, currentDepth + 1) }
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

    // Функция для переключения состояния "раскрыто/свернуто" у папки
    fun toggleNodeExpansion(nodeToToggle: FileTreeNode) {
        _fileTree.value?.let { root ->
            val newRoot = updateNodeExpansion(root, nodeToToggle.uri, !nodeToToggle.isExpanded)
            _fileTree.value = newRoot
        }
    }

    // Рекурсивная функция для обновления состояния узла в дереве
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
}
