package com.amrdeveloper.terminal.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TerminalGreen,
    secondary = TerminalGray,
    tertiary = TerminalGreen
)

private val LightColorScheme = lightColorScheme(
    primary = TerminalGreen,
    secondary = TerminalGray,
    tertiary = TerminalGreen
)

@Composable
fun TerminalTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}