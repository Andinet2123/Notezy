package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NaturalOrangeLight,
    onPrimary = Color.Black,
    primaryContainer = NaturalOrangeContainerDark,
    onPrimaryContainer = NaturalOrangeLight,
    secondary = NaturalOrangeLight,
    onSecondary = Color.Black,
    background = NaturalBgDark,
    onBackground = NaturalTextPrimaryDark,
    surface = NaturalSurfaceDark,
    onSurface = NaturalTextPrimaryDark,
    surfaceVariant = NaturalCardBgDark,
    onSurfaceVariant = NaturalTextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = NaturalOrange,
    onPrimary = Color.White,
    primaryContainer = NaturalOrangeContainer,
    onPrimaryContainer = Color(0xFF9A3412),
    secondary = NaturalOrange,
    onSecondary = Color.White,
    background = NaturalBgLight,
    onBackground = NaturalTextPrimaryLight,
    surface = NaturalSurfaceLight,
    onSurface = NaturalTextPrimaryLight,
    surfaceVariant = NaturalCardBgLight,
    onSurfaceVariant = NaturalTextSecondaryLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to prioritize our signature iOS yellow notes theme
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

