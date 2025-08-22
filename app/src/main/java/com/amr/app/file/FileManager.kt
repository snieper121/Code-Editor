package com.amr.app.file

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileManager(private val context: Context) {
    
    suspend fun getRootDocument(uri: Uri): DocumentFile? {
        return withContext(Dispatchers.IO) {
            try {
                DocumentFile.fromTreeUri(context, uri)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    suspend fun getDocumentFromUri(uri: Uri): DocumentFile? {
        return withContext(Dispatchers.IO) {
            try {
                DocumentFile.fromSingleUri(context, uri)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    suspend fun listChildren(document: DocumentFile): List<DocumentFile> {
        return withContext(Dispatchers.IO) {
            try {
                document.listFiles()
                    .sortedWith(compareBy<DocumentFile>({ !it.isDirectory }, { it.name ?: "" }))
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    suspend fun readFileContent(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
            } catch (e: Exception) {
                null
            }
        }
    }
}