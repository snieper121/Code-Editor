package com.amr.app

import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding // <-- Убедитесь, что этот импорт есть
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import java.util.HashMap

@Composable
fun EditorScreen(navController: NavController) {
    AppTheme {
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
        ) { paddingValues -> // <-- paddingValues создается здесь
            // ИСПОЛЬЗУЕМ ЕГО ВНУТРИ ЭТОГО БЛОКА
            Column(modifier = Modifier.padding(paddingValues)) { // <-- ИСПРАВЛЕНО ЗДЕСЬ
                AndroidView(
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
            }
        }
    }
}
