package com.amr.app

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.util.UUID

data class FileTreeNode(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val uri: Uri,
    val documentFile: DocumentFile?, // добавляем ссылку на DocumentFile
    val isDirectory: Boolean,
    val children: List<FileTreeNode>?,
    val depth: Int,
    val isExpanded: Boolean = false,
    val isLoading: Boolean = false
)