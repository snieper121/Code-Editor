package com.amr.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Добавляем новые маршруты
object Routes {
    const val EDITOR = "editor"
    const val SETTINGS = "settings"
    const val EDITOR_SETTINGS = "editor_settings"
    const val APP_SETTINGS = "app_settings"
    const val TERMINAL_SETTINGS = "terminal_settings"
    const val FORMATTER_SETTINGS = "formatter_settings"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.EDITOR) {
        composable(Routes.EDITOR) { EditorScreen(navController = navController) }
        // Теперь SettingsScreen тоже принимает navController, чтобы переходить дальше
        composable(Routes.SETTINGS) { SettingsScreen(navController = navController) }
        // Регистрируем все новые экраны
        composable(Routes.EDITOR_SETTINGS) { EditorSettingsScreen() }
        composable(Routes.APP_SETTINGS) { AppSettingsScreen() }
        composable(Routes.TERMINAL_SETTINGS) { TerminalSettingsScreen() }
        composable(Routes.FORMATTER_SETTINGS) { FormatterSettingsScreen() }
    }
}
