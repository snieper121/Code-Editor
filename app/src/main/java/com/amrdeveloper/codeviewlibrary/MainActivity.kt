package com.amrdeveloper.codeviewlibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.amr.app.AppNavigation // Импортируем нашу навигацию

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Запускаем навигацию, которая сама решит, какой экран показать
            AppNavigation()
        }
    }
}
