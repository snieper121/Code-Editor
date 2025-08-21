package com.amr.app

import android.graphics.Color
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
    val fileTree by editorViewModel.fileTree.collectAsState()
    
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { uri ->
            uri?.let {
                try {
                    val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(it, takeFlags)
                } catch (_: SecurityException) {
                    // игнорируем: доступ уже может быть выдан или среда вернула меньшие флаги
                }
                editorViewModel.buildFileTreeFromUri(context, it)
            }
        }
    )

    Scaffold(
        scaffoldState = scaffoldState,
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    "Файловый менеджер",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(16.dp)
                )
                Divider()
                fileTree?.let { rootNode ->
                    FileTreeView(
                        root = rootNode,
                        onNodeClick = { node ->
                            if (node.isDirectory) {
                                editorViewModel.toggleNodeExpansion(context, node)
                            } else {
                                editorViewModel.openFileTab(context, node.uri, node.name)
                                scope.launch { scaffoldState.drawerState.close() }
                            }
                        }
                    )
                } ?: Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Загрузка дерева...")
                }
            }
        },
        topBar = {
            TopAppBar(
                modifier = Modifier.height(48.dp),
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
                            folderPickerLauncher.launch(null)
                            showMenu = false
                        }) { Text("Выбрать папку проекта") }
                        Divider()
                        DropdownMenuItem(onClick = {
                            editorViewModel.createNewTab()
                            showMenu = false
                        }) { Text("Новый файл") }
                        Divider()
                        DropdownMenuItem(onClick = { /* TODO */ ; showMenu = false }) { Text("Сохранить") }
                        DropdownMenuItem(onClick = { /* TODO */ ; showMenu = false }) { Text("Сохранить как...") }
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
                ScrollableTabRow(
                    selectedTabIndex = activeTabIndex,
                    edgePadding = 0.dp,
                    divider = {},
                    indicator = {},
                    modifier = Modifier.height(36.dp)
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = activeTabIndex == index,
                            onClick = { editorViewModel.onTabSelected(index) },
                            modifier = Modifier.height(36.dp),
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(tab.name, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
                                    Spacer(Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Закрыть",
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { editorViewModel.closeTab(index) }
                                    )
                                }
                            }
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

@Composable
fun FileTreeView(root: FileTreeNode, onNodeClick: (FileTreeNode) -> Unit) {
    val flattenedTree = remember(root) {
        root.flatten()
    }

    LazyColumn {
        items(flattenedTree) { node ->
            FileTreeItem(node = node, onClick = { onNodeClick(node) })
        }
    }
}
fun FileTreeNode.flatten(): List<FileTreeNode> {
    val list = mutableListOf<FileTreeNode>()
    list.add(this)
    if (this.isExpanded) {
        this.children?.forEach { child ->
            list.addAll(child.flatten())
        }
    }
    return list
}

@Composable
fun FileTreeItem(node: FileTreeNode, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = (node.depth * 16).dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon: ImageVector = if (node.isDirectory) Icons.Outlined.Folder else Icons.Outlined.InsertDriveFile
        if (node.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
        } else {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = node.name,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}