package com.amr.app

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.Text
import androidx.navigation.NavController // <-- Нам понадобится NavController
import com.amr.app.theme.AppTheme

@Composable
fun EditorSettingsScreen(navController: NavController) {
    // Применяем тему НЕПОСРЕДСТВЕННО здесь
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Настройки редактора") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Здесь будут настройки редактора")
            }
        }
    }
}
