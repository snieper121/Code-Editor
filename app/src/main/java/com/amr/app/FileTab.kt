package com.amr.app

import android.net.Uri

// Этот класс описывает одну вкладку
data class FileTab(
    val id: Int, // Уникальный идентификатор
    val name: String, // Имя файла, например, "Untitled-1" или "build.gradle"
    var content: String, // Текущее содержимое файла
    val uri: Uri? = null // Ссылка на реальный файл на диске (null для новых файлов)
)