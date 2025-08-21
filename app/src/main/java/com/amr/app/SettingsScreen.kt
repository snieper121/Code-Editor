package com.amr.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item { SettingsItem("Настройки редактора") { navController.navigate(Routes.EDITOR_SETTINGS) } }
            item { SettingsItem("Настройки приложения") { navController.navigate(Routes.APP_SETTINGS) } }
            item { SettingsItem("Настройки терминала") { navController.navigate(Routes.TERMINAL_SETTINGS) } }
            item { SettingsItem("Форматтер языков") { navController.navigate(Routes.FORMATTER_SETTINGS) } }
            item { SettingsItem("О приложении") { /* TODO: Show dialog */ } }
        }
    }
}

@Composable
private fun SettingsItem(title: String, onClick: () -> Unit) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    )
}

// --- ВСЕ ЭКРАНЫ-ЗАГЛУШКИ ЖИВУТ ЗДЕСЬ, В ОДНОМ ФАЙЛЕ ---
@Composable fun EditorSettingsScreen(navController: NavController) { Text("Экран настроек редактора") }
@Composable fun AppSettingsScreen(navController: NavController) { Text("Экран настроек приложения") }
@Composable fun TerminalSettingsScreen(navController: NavController) { Text("Экран настроек терминала") }
@Composable fun FormatterSettingsScreen(navController: NavController) { Text("Экран настроек форматтера") }
