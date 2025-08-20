package com.amr.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Определяем маршруты для наших экранов
object Routes {
    const val EDITOR = "editor"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavigation() {
    // Создаем контроллер, который будет управлять навигацией
    val navController = rememberNavController()

    // NavHost - это контейнер, который будет отображать нужный экран
    NavHost(navController = navController, startDestination = Routes.EDITOR) {
        // Описываем экран редактора
        composable(Routes.EDITOR) {
            // Передаем navController в EditorScreen, чтобы он мог переходить на другие экраны
            EditorScreen(navController = navController)
        }
        // Описываем экран настроек
        composable(Routes.SETTINGS) {
            SettingsScreen()
        }
    }
}