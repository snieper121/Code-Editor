package com.amrdeveloper.codeviewlibrary

import android.os.Bundle
import android.os.Environment
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amr.app.EditorScreen
import com.amr.app.PermissionScreen
import com.amr.app.Routes
import com.amr.app.SettingsScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var hasPermission by remember { mutableStateOf(Environment.isExternalStorageManager()) }

            if (hasPermission) {
                MainAppNavigation()
            } else {
                PermissionScreen {
                    hasPermission = true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Проверяем снова, когда пользователь возвращается из настроек
        setContent {
            var hasPermission by remember { mutableStateOf(Environment.isExternalStorageManager()) }
            if (hasPermission) {
                MainAppNavigation()
            } else {
                PermissionScreen {
                    hasPermission = true
                }
            }
        }
    }
}

@Composable
fun MainAppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.EDITOR) {
        composable(Routes.EDITOR) { EditorScreen(navController) }
        composable(Routes.SETTINGS) { SettingsScreen(navController) }
        // ... другие маршруты ...
    }
}
/*
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Просто запускаем навигацию
            AppNavigation()
        }
    }
}*/