package com.amr.app

import android.graphics.Color
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.amr.app.theme.AppTheme
import com.amrdeveloper.codeview.Code
import com.amrdeveloper.codeviewlibrary.CustomCodeViewAdapter
import com.amrdeveloper.codeviewlibrary.R
import com.amrdeveloper.codeviewlibrary.syntax.LanguageManager
import com.amrdeveloper.codeviewlibrary.syntax.LanguageName
import com.amrdeveloper.codeviewlibrary.syntax.ThemeName
import kotlinx.coroutines.launch
import java.util.HashMap

@Composable
fun EditorScreen(
    navController: NavController,
    editorViewModel: EditorViewModel = viewModel()
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    val tabs by editorViewModel.tabs.collectAsState()
    val activeTabIndex by editorViewModel.activeTabIndex.collectAsState()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val fileName = it.path?.substringAfterLast('/') ?: "file"
                val content = context.contentResolver.openInputStream(it)?.bufferedReader()?.readText() ?: ""
                editorViewModel.openFileTab(fileName, content)
            }
        }
    )

    AppTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
            drawerContent = {
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
                    Text("Здесь будет структура файлов...")
                }
            },
            topBar = {
                TopAppBar(
                    title = { Text("Code Editor") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Файловый менеджер")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Действия")
                        }
                        // --- ВОЗВРАЩАЕМ ВСЕ ПУНКТЫ МЕНЮ ---
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(onClick = {
                                filePickerLauncher.launch("*/*")
                                showMenu = false
                            }) { Text("Выбрать файл") }

                            DropdownMenuItem(onClick = { /* TODO */ ; showMenu = false }) {
                                Text("Выбрать папку")
                            }
                            DropdownMenuItem(onClick = {
                                editorViewModel.createNewTab()
                                showMenu = false
                            }) { Text("Новый файл") }

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
                            }) { Text("Настройки") }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                if (tabs.isNotEmpty()) {
                    ScrollableTabRow(selectedTabIndex = activeTabIndex) {
                        tabs.forEachIndexed { index, tab ->
                            Tab(
                                selected = activeTabIndex == index,
                                onClick = { editorViewModel.onTabSelected(index) },
                                text = { Text(tab.name, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            )
                        }
                    }

                    val activeTab = tabs.getOrNull(activeTabIndex)
                    if (activeTab != null) {
                        CodeViewForTab(
                            content = activeTab.content,
                            onContentChange = { newContent ->
                                editorViewModel.onContentChanged(newContent)
                            }
                        )
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Нет открытых файлов. Создайте новый или выберите существующий.")
                    }
                }
            }
        }
    }
}

@Composable
fun CodeViewForTab(content: String, onContentChange: (String) -> Unit) {
    val codeViewRef = remember { mutableStateOf<com.amrdeveloper.codeview.CodeView?>(null) }

    LaunchedEffect(content) {
        val view = codeViewRef.value
        if (view != null && view.text.toString() != content) {
            view.setText(content)
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            com.amrdeveloper.codeview.CodeView(context).apply {
                // --- ВОЗВРАЩАЕМ ВСЮ ЛОГИКУ НАСТРОЙКИ СТИЛЕЙ ---
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
                // ----------------------------------------------------

                addTextChangedListener(object : android.text.TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: android.text.Editable?) {
                        onContentChange(s.toString())
                    }
                })
                codeViewRef.value = this
            }
        }
    )
}