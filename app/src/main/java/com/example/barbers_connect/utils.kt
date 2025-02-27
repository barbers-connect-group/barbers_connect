package com.example.barbers_connect

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

fun formatIsoDate(isoDate: String): String {
    val parts = isoDate.split("T")
    if (parts.size < 2) return isoDate

    val datePart = parts[0].split("-")
    val timePart = parts[1].substring(0, 5)

    return "${datePart[2]}/${datePart[1]}/${datePart[0]} às $timePart"
}

fun getColorScheme(): ColorScheme {
    return ColorScheme(
        primary = Color(0xFF795548),
        onPrimary = Color.White,
        primaryContainer = Color.White,
        onPrimaryContainer = Color.White,
        inversePrimary = Color.White,
        secondary = Color(0xFF5D4037),
        onSecondary = Color.White,
        secondaryContainer = Color.White,
        onSecondaryContainer = Color.White,
        tertiary = Color(0xFF4E342E),
        onTertiary = Color.White,
        tertiaryContainer = Color.White,
        onTertiaryContainer = Color.White,
        background = Color(0xFFF5F5DC),
        onBackground = Color.Black,
        surface = Color(0xFFF5F5DC),
        onSurface = Color.Black,
        surfaceVariant = Color.White,
        onSurfaceVariant = Color.White,
        surfaceTint = Color.White,
        inverseSurface = Color.White,
        inverseOnSurface = Color.White,
        error = Color.Red,
        onError = Color.White,
        errorContainer = Color.White,
        onErrorContainer = Color.White,
        outline = Color.White,
        outlineVariant = Color.White,
        scrim = Color.White,
        surfaceBright = Color.White,
        surfaceDim = Color.White,
        surfaceContainer = Color.White,
        surfaceContainerHigh = Color.White,
        surfaceContainerHighest = Color.White,
        surfaceContainerLow = Color.White,
        surfaceContainerLowest = Color.White,
    )
}
