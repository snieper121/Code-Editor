package com.amrdeveloper.codeviewlibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amr.app.EditorScreen
import com.amr.app.Routes
import com.amr.app.SettingsScreen
import com.amr.app.theme.AppTheme
import com.amr.app.settings.EditorSettingsScreen
import com.amr.app.settings.AppSettingsScreen
import com.amr.app.settings.TerminalSettingsScreen
import com.amr.app.settings.FormatterSettingsScreen
import com.amrdeveloper.terminal.ui.TerminalScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainAppNavigation()
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
        composable("terminal") { TerminalScreen(navController) }

        // --- РЕГИСТРИРУЕМ ВСЕ ЭКРАНЫ-ЗАГЛУШКИ ---
        composable(Routes.EDITOR_SETTINGS) { EditorSettingsScreen(navController) }
        composable(Routes.APP_SETTINGS) { AppSettingsScreen(navController) }
        composable(Routes.TERMINAL_SETTINGS) { TerminalSettingsScreen(navController) }
        composable(Routes.FORMATTER_SETTINGS) { FormatterSettingsScreen(navController) }
    }
}
