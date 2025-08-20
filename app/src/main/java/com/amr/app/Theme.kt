package com.amr.app.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Определяем нашу темную палитру
private val DarkColorPalette = darkColors(
    primary = Color(0xFFBB86FC),    // Фиолетовый (акцентный)
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),      // Бирюзовый (вторичный акцентный)
    background = Color(0xFF121212), // Очень темный фон
    surface = Color(0xFF121212),    // Фон для карточек, диалогов и т.д.
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,   // Цвет текста на темном фоне
    onSurface = Color.White,      // Цвет текста на "карточках"
)

// Наша главная тема приложения
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography, // (пока не создаем, но оставляем для будущего)
        shapes = Shapes,       // (пока не создаем, но оставляем для будущего)
        content = content
    )
}
