package com.amrdeveloper.terminal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    navController: NavController,
    isDarkTheme: Boolean
) {
    val bg = if (isDarkTheme) Color.Black else Color.White
    val fg = if (isDarkTheme) Color.White else Color.Black
    val hint = if (isDarkTheme) Color(0xFFAAAAAA) else Color(0xFF444444)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Terminal",
            color = fg, /* ... */
        /*Text(
            text = "Terminal",
            color = Color.White,  // ← Светлые символы
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp*/
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Terminal module is ready!",
            color = Color.White,  // ← Светлые символы
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Go to Settings → Terminal Settings to customize colors",
            color = hint,
        /*
        Text(
            text = "Go to Settings → Terminal Settings to customize colors",
            color = Color.Gray,  // ← Серый текст для подсказки
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp*/
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { navController.navigateUp() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Black
                )
            ) {
                Text("Back")
            }
            
            Button(
                onClick = { 
                    navController.navigate("terminal_settings") 
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text("Settings")
            }
        }
    }
}