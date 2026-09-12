package com.sahih.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SahihColorScheme = darkColorScheme(
    background = Ink,
    surface = Surface,
    surfaceVariant = CardBg,
    primary = Gold,
    onPrimary = Ink,
    secondary = Jade,
    error = Coral,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = Hairline,
)

@Composable
fun SahihTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Sahih is always dark-themed regardless of system setting -- the
    // navy/gold palette is core to the brand, similar to how banking
    // apps keep a consistent identity.
    MaterialTheme(
        colorScheme = SahihColorScheme,
        typography = SahihTypography,
        content = content
    )
}
