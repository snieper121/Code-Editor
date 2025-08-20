package com.amr.app

import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import com.amrdeveloper.codeview.Code
import com.amrdeveloper.codeviewlibrary.CustomCodeViewAdapter
import com.amrdeveloper.codeviewlibrary.R
import com.amrdeveloper.codeviewlibrary.syntax.LanguageManager
import com.amrdeveloper.codeviewlibrary.syntax.LanguageName
import com.amrdeveloper.codeviewlibrary.syntax.ThemeName
import java.util.HashMap

// Теперь наш экран принимает NavController
@Composable
fun EditorScreen(navController: NavController) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Code Editor") },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(onClick = { /* TODO */ ; showMenu = false }) {
                            Text("Save File")
                        }
                        // НОВЫЙ ПУНКТ МЕНЮ
                        DropdownMenuItem(onClick = {
                            navController.navigate(Routes.SETTINGS) // Переходим на экран настроек
                            showMenu = false
                        }) {
                            Text("Настройки")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        // ИСПРАВЛЕНИЕ ПРОБЛЕМЫ С ЦЕНТРИРОВАНИЕМ
        Column(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                // Modifier.weight(1f) заставляет этот элемент занять все оставшееся место
                modifier = Modifier.fillMaxSize().weight(1f),
                factory = { context ->
                    com.amrdeveloper.codeview.CodeView(context).apply {
                        // ... вся логика настройки CodeView остается без изменений ...
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
            // Сюда мы позже добавим нижнюю панель с символами
        }
    }
}
