package com.example.studyapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color

/*
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

 */

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
// Light Theme Colors
    private val LightColorScheme = lightColorScheme(
        primary = Color(0xFF006875),
        onPrimary = Color.White,
        primaryContainer = Color(0xFF97F0FF),
        onPrimaryContainer = Color(0xFF001F24),

        secondary = Color(0xFF4A6268),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFCDE7EC),
        onSecondaryContainer = Color(0xFF051F24),

        background = Color(0xFFFBFCFD),
        onBackground = Color(0xFF191C1D),

        surface = Color(0xFFFBFCFD),
        onSurface = Color(0xFF191C1D),

        error = Color(0xFFBA1A1A),
        onError = Color.White,
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002)
    )

// Dark Theme Colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4FD8EB),
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF97F0FF),

    secondary = Color(0xFFB1CBD0),
    onSecondary = Color(0xFF1C3439),
    secondaryContainer = Color(0xFF334A50),
    onSecondaryContainer = Color(0xFFCDE7EC),

    background = Color(0xFF191C1D),
    onBackground = Color(0xFFE1E3E4),

    surface = Color(0xFF191C1D),
    onSurface = Color(0xFFE1E3E4),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)


@Composable
fun StudyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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