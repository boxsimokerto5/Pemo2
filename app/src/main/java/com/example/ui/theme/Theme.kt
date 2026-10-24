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
    primary = SipemoEmerald,
    onPrimary = SipemoNavyDark,
    primaryContainer = SipemoEmeraldDark,
    onPrimaryContainer = Color.White,
    secondary = SipemoAmber,
    onSecondary = SipemoNavyDark,
    background = SipemoNavyDark,
    onBackground = Color.White,
    surface = SipemoNavyCard,
    onSurface = Color.White,
    surfaceVariant = SipemoNavyBorder,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = SipemoNavyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = SipemoEmeraldDark,
    onPrimary = Color.White,
    primaryContainer = SipemoEmeraldLight,
    onPrimaryContainer = Color(0xFF065F46),
    secondary = SipemoBlue,
    onSecondary = Color.White,
    secondaryContainer = SipemoBlueLight,
    onSecondaryContainer = Color(0xFF1E40AF),
    tertiary = SipemoAmber,
    background = SipemoBgLight,
    onBackground = SipemoTextPrimary,
    surface = SipemoSurfaceWhite,
    onSurface = SipemoTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = SipemoTextSecondary,
    outline = Color(0xFFE2E8F0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
