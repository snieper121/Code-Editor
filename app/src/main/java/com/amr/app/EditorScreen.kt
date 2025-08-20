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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
    editorViewModel: EditorViewModel = viewModel() // Получаем экземпляр ViewModel
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    // Собираем состояния из ViewModel
    val tabs by editorViewModel.tabs.collectAsState()
    val activeTabIndex by editorViewModel.activeTabIndex.collectAsState()

    // Создаем лаунчер для выбора файла
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                // Читаем содержимое файла и его имя
                val fileName = it.path?.substringAfterLast('/') ?: "file"
                val content = context.contentResolver.openInputStream(it)?.bufferedReader()?.readText() ?: ""
                editorViewModel.openFileTab(fileName, content)
            }
        }
    )

    AppTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = { /* ... без изменений ... */ },
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
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(onClick = {
                                filePickerLauncher.launch("*/*") // Запускаем выбор файла
                                showMenu = false
                            }) { Text("Выбрать файл") }

                            // ... другие пункты меню ...

                            DropdownMenuItem(onClick = {
                                editorViewModel.createNewTab() // Вызываем метод ViewModel
                                showMenu = false
                            }) { Text("Новый файл") }

                            // ... другие пункты меню ...
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                // --- НАША НОВАЯ ПАНЕЛЬ ВКЛАДОК ---
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

                    // --- РЕДАКТОР КОДА ДЛЯ АКТИВНОЙ ВКЛАДКИ ---
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
                    // Показываем заглушку, если нет открытых вкладок
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("Нет открытых файлов. Создайте новый или выберите существующий.")
                    }
                }
            }
        }
    }
}

// Выносим CodeView в отдельный Composable для чистоты
@Composable
fun CodeViewForTab(content: String, onContentChange: (String) -> Unit) {
    val codeViewRef = remember { mutableStateOf<com.amrdeveloper.codeview.CodeView?>(null) }

    // Обновляем текст в CodeView, только если он изменился извне
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
                // ... вся логика настройки CodeView ...
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
