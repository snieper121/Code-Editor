package com.amr.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
        composable(Routes.SETTINGS) { SettingsScreen(navController = navController) }

        // --- ИЗМЕНЕНИЯ ЗДЕСЬ ---
        // Теперь мы передаем navController в каждый из дочерних экранов
        composable(Routes.EDITOR_SETTINGS) { EditorSettingsScreen(navController = navController) }
        composable(Routes.APP_SETTINGS) { AppSettingsScreen(navController = navController) }
        composable(Routes.TERMINAL_SETTINGS) { TerminalSettingsScreen(navController = navController) }
        composable(Routes.FORMATTER_SETTINGS) { FormatterSettingsScreen(navController = navController) }
    }
}
