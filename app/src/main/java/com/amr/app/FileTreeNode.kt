package com.amr.app

data class FileTreeNode(
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val children: List<FileTreeNode>,
    val depth: Int,
    val isExpanded: Boolean = false
)
