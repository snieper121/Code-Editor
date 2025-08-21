package com.amr.app

import android.net.Uri

data class FileTreeNode(
    val name: String,
    val uri: Uri,
    val isDirectory: Boolean,
    val children: List<FileTreeNode>,
    val depth: Int,
    val isExpanded: Boolean = false
)
