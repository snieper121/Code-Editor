package com.amr.app

import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.* // Импортируем все из Material
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import com.amrdeveloper.codeview.Code
import com.amrdeveloper.codeviewlibrary.CustomCodeViewAdapter
import com.amrdeveloper.codeviewlibrary.R
import com.amrdeveloper.codeviewlibrary.syntax.LanguageManager
import com.amrdeveloper.codeviewlibrary.syntax.LanguageName
import com.amrdeveloper.codeviewlibrary.syntax.ThemeName
import java.util.HashMap

@Composable
fun EditorScreen() {
    // Состояние для отслеживания, открыто ли меню
    var showMenu by remember { mutableStateOf(false) }

    // Scaffold - это базовый макет Material Design, который дает нам TopAppBar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Code Editor") },
                actions = {
                    // Кнопка "три точки"
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    // Выпадающее меню
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        // Наш пункт меню "Save File"
                        DropdownMenuItem(onClick = {
                            /* TODO: Implement save logic */
                            showMenu = false // Закрываем меню после нажатия
                        }) {
                            Text("Save File")
                        }
                        // Сюда можно будет добавить и другие пункты (выбор языка и т.д.)
                    }
                }
            )
        }
    ) { paddingValues -> // paddingValues - это отступы, которые оставляет TopAppBar
        // Вся логика настройки редактора остается здесь, как в Шаге 1
        AndroidView(
            modifier = Modifier.fillMaxSize(),
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
