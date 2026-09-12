package com.iicemeta.wheat.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AutumnOrange,
    onPrimary = Wheat50,
    primaryContainer = Wheat100,
    onPrimaryContainer = Wheat900,
    secondary = Wheat600,
    onSecondary = Wheat50,
    secondaryContainer = Wheat200,
    onSecondaryContainer = Wheat900,
    tertiary = AutumnGreen,
    onTertiary = Wheat50,
    background = Wheat50,
    onBackground = AutumnDark,
    surface = Wheat50,
    onSurface = AutumnDark,
    surfaceVariant = Wheat100,
    onSurfaceVariant = AutumnMuted,
    error = GranaryEmpty,
)

@Composable
fun WheatTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Wheat100.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
