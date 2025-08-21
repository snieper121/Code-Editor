package com.amr.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.amr.app.theme.AppTheme

@Composable
fun SettingsScreen(navController: NavController) {
    AppTheme {
        var showAboutDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Настройки") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                item { SettingsItem("Настройки редактора") { navController.navigate(Routes.EDITOR_SETTINGS) } }
                item { SettingsItem("Настройки приложения") { navController.navigate(Routes.APP_SETTINGS) } }
                item { SettingsItem("Настройки терминала") { navController.navigate(Routes.TERMINAL_SETTINGS) } }
                item { SettingsItem("Форматтер языков") { navController.navigate(Routes.FORMATTER_SETTINGS) } }
                item { SettingsItem("О приложении") { showAboutDialog = true } }
            }
        }

        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text("О приложении") },
                text = { Text("Code Editor v0.1\nСоздано с помощью Manus AI") },
                confirmButton = {
                    Button(onClick = { showAboutDialog = false }) {
                        Text("OK")
                    }
                }
            )
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
    Divider()
}
