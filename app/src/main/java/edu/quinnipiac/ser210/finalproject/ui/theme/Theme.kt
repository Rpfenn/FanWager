package edu.quinnipiac.ser210.finalproject.ui.theme

import android.app.Activity
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
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

enum class ColorSchemeType {
    LIGHT, DARK, BLUE
}

@Composable
fun FinalProjectTheme(
    colorSchemeType: ColorSchemeType = if (isSystemInDarkTheme()) ColorSchemeType.DARK else ColorSchemeType.LIGHT,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (colorSchemeType == ColorSchemeType.DARK)
                dynamicDarkColorScheme(context)
            else
                dynamicLightColorScheme(context)
        }
        colorSchemeType == ColorSchemeType.DARK -> DarkColorScheme
        colorSchemeType == ColorSchemeType.BLUE -> lightColorScheme(
            primary = Color(0xFF0288D1),
            secondary = Color(0xFF03A9F4),
            tertiary = Color(0xFF81D4FA),
            background = Color(0xFFE1F5FE),
            surface = Color(0xFFE1F5FE)
        )
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}