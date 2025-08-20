package com.amr.app

import android.net.Uri

// Описывает один узел в файловом дереве
data class FileTreeNode(
    val name: String,
    val uri: Uri,
    val isDirectory: Boolean,
    val children: List<FileTreeNode> = emptyList(),
    val depth: Int = 0, // Глубина вложенности для отступов
    var isExpanded: Boolean = false // Раскрыта ли папка
)