package com.amrdeveloper.codeviewlibrary

import android.os.Bundle
import androidx.activity.ComponentActivity // <-- ВАЖНО: Меняем AppCompatActivity на ComponentActivity
import androidx.activity.compose.setContent
import com.amr.app.EditorScreen // <-- ВАЖНО: Убираем суффикс Kt

// Теперь это Kotlin-класс
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Вызываем наш Compose-экран.
        // Так как мы в Kotlin, синтаксис становится проще.
        setContent {
            EditorScreen()
        }
    }
}
