package com.amr.app.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
    var fontSizeText by remember { mutableStateOf(vm.editorFontSizePx.value.toString()) }
    var scaleText by remember { mutableStateOf(String.format("%.1f", vm.editorScale.value)) }
    
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
                Text("Размер шрифта (px)", style = MaterialTheme.typography.h6)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = fontSizeText,
                    onValueChange = { 
                        fontSizeText = it
                        it.toIntOrNull()?.let { size ->
                            if (size in 8..40) vm.setEditorFontSizePx(size)
                        }
                    },
                    label = { Text("Размер шрифта") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))
            }
            
            item {
                Text("Масштаб редактора", style = MaterialTheme.typography.h6)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = scaleText,
                    onValueChange = { 
                        scaleText = it
                        it.toFloatOrNull()?.let { scale ->
                            if (scale in 0.5f..2.0f) vm.setEditorScale(scale)
                        }
                    },
                    label = { Text("Масштаб (0.5 - 2.0)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))
            }
            
            item {
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
    
    // Обновляем текстовые поля при изменении значений
    LaunchedEffect(vm.editorFontSizePx.value) {
        fontSizeText = vm.editorFontSizePx.value.toString()
    }
    
    LaunchedEffect(vm.editorScale.value) {
        scaleText = String.format("%.1f", vm.editorScale.value)
    }
}