package com.amr.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.amrdeveloper.codeview.CodeView

@Composable
fun EditorScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        // 1. Наша новая кнопка, созданная с помощью Compose
        Button(onClick = { /* TODO: Implement save logic */ }) {
            Text("Save File")
        }

        // 2. Встраиваем старый CodeView в наш новый Compose-экран
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                // Здесь мы создаем экземпляр старого CodeView
                CodeView(context).apply {
                    // Сюда мы позже перенесем всю логику настройки
                    // из MainActivity (шрифты, цвета, плагины и т.д.)
                    // Пока он будет просто пустым редактором.
                }
            }
        )
    }
}
