package com.amr.app.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check

@Composable
fun EditorSettingsScreen(navController: NavController, vm: SettingsViewModel = viewModel()) {
    val fontSizeOptions = listOf(12, 14, 16, 18, 20, 22, 24, 26, 28, 30)
    val scaleOptions = listOf(0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.4f)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки редактора") },
                navigationIcon = { 
                    IconButton(onClick = { navController.navigateUp() }) { 
                        Icon(Icons.Default.ArrowBack, null) 
                    } 
                }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp)) {
            item {
                Text("Размер шрифта", style = MaterialTheme.typography.h6)
                Spacer(Modifier.height(8.dp))
            }
            
            items(fontSizeOptions) { size ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { vm.setEditorFontSizePx(size) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${size}px")
                    if (vm.editorFontSizePx.value == size) {
                        Icon(Icons.Default.Check, contentDescription = "Выбрано")
                    }
                }
                Divider()
            }
            
            item {
                Spacer(Modifier.height(16.dp))
                Text("Масштаб редактора", style = MaterialTheme.typography.h6)
                Spacer(Modifier.height(8.dp))
            }
            
            items(scaleOptions) { scale ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { vm.setEditorScale(scale) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${String.format("%.1f", scale)}x")
                    if (vm.editorScale.value == scale) {
                        Icon(Icons.Default.Check, contentDescription = "Выбрано")
                    }
                }
                Divider()
            }
            
            item {
                Spacer(Modifier.height(16.dp))
                Text("Дополнительные настройки", style = MaterialTheme.typography.h6)
                Spacer(Modifier.height(8.dp))
            }
            
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Номера строк")
                    Switch(
                        checked = vm.editorLineNumbers.value, 
                        onCheckedChange = { vm.setEditorLineNumbers(it) }
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
            
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Подсветка текущей строки")
                    Switch(
                        checked = vm.editorHighlightLine.value, 
                        onCheckedChange = { vm.setEditorHighlightLine(it) }
                    )
                }
            }
        }
    }
}