package com.amr.app

import android.net.Uri
import java.util.UUID

data class FileTab(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri? = null, // Uri файла, если он есть
    val name: String,
    val content: String
)
