package com.amrdeveloper.codeviewlibrary

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amr.app.EditorScreen
import com.amr.app.PermissionScreen
import com.amr.app.Routes
import com.amr.app.SettingsScreen
import com.amr.app.theme.AppTheme
import androidx.activity.compose.rememberLauncherForActivityResult

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppEntry()
        }
    }
}

@Composable
fun AppEntry() {
    var hasPermission by remember { mutableStateOf(Environment.isExternalStorageManager()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // Когда пользователь возвращается из настроек, мы снова проверяем разрешение
        hasPermission = Environment.isExternalStorageManager()
    }

    AppTheme {
        if (hasPermission) {
            // Если разрешение есть, показываем основное приложение
            MainAppNavigation()
        } else {
            // Если разрешения нет, показываем экран с просьбой
            PermissionScreen(
                onGoToSettingsClicked = {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:com.amrdeveloper.codeviewlibrary")
                    permissionLauncher.launch(intent)
                }
            )
        }
    }
}

@Composable
fun MainAppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.EDITOR) {
        composable(Routes.EDITOR) { EditorScreen(navController) }
        composable(Routes.SETTINGS) { SettingsScreen(navController) }
        // Другие экраны настроек, если они есть
    }
}
