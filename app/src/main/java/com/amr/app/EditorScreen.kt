package com.amr.app

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import com.amr.app.theme.AppTheme
import com.amrdeveloper.codeview.Code
import com.amrdeveloper.codeviewlibrary.CustomCodeViewAdapter
import com.amrdeveloper.codeviewlibrary.R
import com.amrdeveloper.codeviewlibrary.syntax.LanguageManager
import com.amrdeveloper.codeviewlibrary.syntax.LanguageName
import com.amrdeveloper.codeviewlibrary.syntax.ThemeName
import kotlinx.coroutines.launch // <-- ВАЖНЫЙ ИМПОРТ
import java.util.HashMap

@Composable
fun EditorScreen(navController: NavController) {
    AppTheme {
        // --- НОВЫЕ СОСТОЯНИЯ ДЛЯ SCAFFOLD И DRAWER ---
        val scaffoldState = rememberScaffoldState() // Хранит состояние (открыт/закрыт drawer)
        val scope = rememberCoroutineScope() // Для запуска анимации открытия/закрытия

        var showMenu by remember { mutableStateOf(false) }

        Scaffold(
            // --- ДОБАВЛЯЕМ НОВЫЕ ПАРАМЕТРЫ В SCAFFOLD ---
            scaffoldState = scaffoldState, // Передаем состояние в Scaffold
            drawerContent = {
                // --- ЭТО СОДЕРЖИМОЕ НАШЕЙ ВЫДВИЖНОЙ ПАНЕЛИ ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Файловый менеджер",
                        style = MaterialTheme.typography.h6
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    // Сюда мы в будущем добавим список файлов и папок
                    Text("Здесь будет структура файлов...")
                }
            },
            topBar = {
                TopAppBar(
                    title = { Text("Code Editor") },
                    navigationIcon = {
                        // --- ОЖИВЛЯЕМ КНОПКУ "ГАМБУРГЕР" ---
                        IconButton(onClick = {
                            // Запускаем корутину для плавного открытия панели
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Открыть файловый менеджер"
                            )
                        }
                    },
                    actions = {
                        // ... остальной код для actions (кнопка "три точки" и меню) без изменений ...
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Действия")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(onClick = { /* TODO */ ; showMenu = false }) {
                                Text("Выбрать файл")
                            }
                            DropdownMenuItem(onClick = { /* TODO */ ; showMenu = false }) {
                                Text("Выбрать папку")
                            }
                            DropdownMenuItem(onClick = { /* TODO */ ; showMenu = false }) {
                                Text("Новый файл")
                            }
                            Divider()
                            DropdownMenuItem(onClick = { /* TODO */ ; showMenu = false }) {
                                Text("Сохранить")
                            }
                            DropdownMenuItem(onClick = { /* TODO */ ; showMenu = false }) {
                                Text("Сохранить как...")
                            }
                            Divider()
                            DropdownMenuItem(onClick = {
                                navController.navigate(Routes.SETTINGS)
                                showMenu = false
                            }) {
                                Text("Настройки")
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            // ... остальной код (Column и AndroidView) без изменений ...
            Column(modifier = Modifier.padding(paddingValues)) {
                AndroidView(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    factory = { context ->
                        com.amrdeveloper.codeview.CodeView(context).apply {
                            setBackgroundColor(Color.parseColor("#212121"))
                            val jetBrainsMono = ResourcesCompat.getFont(context, R.font.jetbrains_mono_medium)
                            this.typeface = jetBrainsMono
                            this.setEnableLineNumber(true)
                            this.setLineNumberTextColor(Color.GRAY)
                            this.setLineNumberTextSize(25f)
                            this.setEnableHighlightCurrentLine(true)
                            this.setHighlightCurrentLineColor(Color.DKGRAY)
                            this.setTabLength(4)
                            this.setEnableAutoIndentation(true)
                            val languageManager = LanguageManager(context, this)
                            languageManager.applyTheme(LanguageName.JAVA, ThemeName.MONOKAI)
                            val codeList: List<Code> = languageManager.getLanguageCodeList(LanguageName.JAVA)
                            val adapter = CustomCodeViewAdapter(context, codeList)
                            this.setAdapter(adapter)
                            val pairCompleteMap: MutableMap<Char, Char> = HashMap()
                            pairCompleteMap['{'] = '}'
                            pairCompleteMap['['] = ']'
                            pairCompleteMap['('] = ')'
                            pairCompleteMap['<'] = '>'
                            pairCompleteMap['"'] = '"'
                            pairCompleteMap['\''] = '\''
                            this.setPairCompleteMap(pairCompleteMap)
                            this.enablePairComplete(true)
                            this.enablePairCompleteCenterCursor(true)
                        }
                    }
                )
            }
        }
    }
}
