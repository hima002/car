package com.hima.alwarsha.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/** Single fixed visual identity for "الورشة" — no theme gallery, no light mode toggle. */
data class ThemeStyle(
    val canvasBg: Color = CanvasBg,
    val cardBg: Color = CardBg,
    val cardBorderColor: Color = CardBorder,
    val navBg: Color = NavBg,
    val textPrimary: Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val primaryColor: Color = Amber,
    val primaryDarkColor: Color = AmberDark,
    val cardCornerRadius: Dp = 16.dp,
    val cardBorderWidth: Dp = 1.dp,
    val cardShape: Shape = RoundedCornerShape(16.dp)
)

val LocalThemeStyle = staticCompositionLocalOf { ThemeStyle() }

private val AlWarshaColorScheme = darkColorScheme(
    primary = Amber,
    onPrimary = Color.Black,
    secondary = AmberDark,
    background = CanvasBg,
    onBackground = TextPrimary,
    surface = CardBg,
    onSurface = TextPrimary,
    surfaceVariant = NavBg,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = StatusRed
)

@Composable
fun AlWarshaTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalThemeStyle provides ThemeStyle(),
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        MaterialTheme(
            colorScheme = AlWarshaColorScheme,
            typography = AlWarshaTypography,
            content = content
        )
    }
}
