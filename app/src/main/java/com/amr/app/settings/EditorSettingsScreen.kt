package com.amr.app.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun EditorSettingsScreen(navController: NavController, vm: SettingsViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки редактора") },
                navigationIcon = { IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Размер шрифта: ${vm.editorFontSizeSp.value.toInt()} sp")
            Slider(
                value = vm.editorFontSizeSp.value,
                onValueChange = { vm.setEditorFontSizeSp(it) },
                valueRange = 10f..24f,
                steps = 14,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Text("Масштаб редактора: ${"%.2f".format(vm.editorScale.value)}x")
            Slider(
                value = vm.editorScale.value,
                onValueChange = { vm.setEditorScale(it) },
                valueRange = 0.8f..1.4f,
                steps = 12,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Номера строк")
                Switch(checked = vm.editorLineNumbers.value, onCheckedChange = { vm.setEditorLineNumbers(it) })
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Подсветка текущей строки")
                Switch(checked = vm.editorHighlightLine.value, onCheckedChange = { vm.setEditorHighlightLine(it) })
            }
        }
    }
}