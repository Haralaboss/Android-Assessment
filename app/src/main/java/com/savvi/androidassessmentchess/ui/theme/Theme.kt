package com.savvi.androidassessmentchess.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = LightGreyPrimary,
    secondary = OffWhite,
    tertiary = White,
    background = DarkBlueBackground,
    surface = DarkBlueBackground,
    onBackground = OffWhite,
    onSurface = OffWhite,
    onPrimary = DarkBlueBackground,
    onSecondary = DarkBlueBackground
)

private val LightColorScheme = lightColorScheme(
    primary = LightGreyPrimary,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = DarkBlueBackground,
    surface = DarkBlueBackground,
    onBackground = OffWhite,
    onSurface = OffWhite
)

@Composable
fun AndroidAssessmentChessTheme(
    darkTheme: Boolean = true, // Force dark theme as requested
    dynamicColor: Boolean = false, // Disable dynamic color to maintain the requested palette
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
