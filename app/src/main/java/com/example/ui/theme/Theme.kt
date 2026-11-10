package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CrimsonSun,
    onPrimary = TextPrimary,
    primaryContainer = CrimsonSunDark,
    onPrimaryContainer = TextPrimary,
    secondary = FlameGold,
    onSecondary = DemonSlayerBlack,
    secondaryContainer = DemonSlayerCardElevated,
    onSecondaryContainer = FlameGold,
    tertiary = WaterBlue,
    onTertiary = DemonSlayerBlack,
    background = DemonSlayerBlack,
    onBackground = TextPrimary,
    surface = DemonSlayerDark,
    onSurface = TextPrimary,
    surfaceVariant = DemonSlayerCard,
    onSurfaceVariant = TextSecondary,
    outline = DemonSlayerBorder,
    outlineVariant = DemonSlayerBorderGlow
)

@Composable
fun SamuraiTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = DemonSlayerBlack.toArgb()
                it.navigationBarColor = DemonSlayerBlack.toArgb()
                val controller = WindowCompat.getInsetsController(it, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SamuraiTypography,
        content = content
    )
}
