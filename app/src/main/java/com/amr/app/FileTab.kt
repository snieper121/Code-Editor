package com.amr.app

import java.util.UUID

data class FileTab(
    val id: String = UUID.randomUUID().toString(),
    val path: String? = null, // Путь к файлу, если он есть
    val name: String,
    val content: String
)
