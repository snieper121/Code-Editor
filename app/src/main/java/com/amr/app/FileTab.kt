package com.amr.app

import android.net.Uri
import java.util.UUID

data class FileTab(
    val id: String = UUID.randomUUID().toString(), // Уникальный ID для каждой вкладки
    val name: String,
    val content: String
)
