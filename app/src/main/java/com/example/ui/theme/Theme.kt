package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun AutoKeepTheme(
    appTheme: AppTheme = AppTheme.CYBER_NEON,
    appLanguage: AppLanguage = AppLanguage.AR,
    content: @Composable () -> Unit
) {
    val buttonShape = when {
        appTheme.cardCornerRadius >= 24.dp -> RoundedCornerShape(50)
        appTheme.cardCornerRadius <= 6.dp -> RoundedCornerShape(4.dp)
        else -> RoundedCornerShape(10.dp)
    }

    val themeStyle = ThemeStyle(
        theme = appTheme,
        cardCornerRadius = appTheme.cardCornerRadius,
        cardShape = RoundedCornerShape(appTheme.cardCornerRadius),
        buttonShape = buttonShape,
        chipShape = buttonShape,
        cardBorderWidth = appTheme.cardBorderWidth,
        cardBorderColor = appTheme.cardBorderColor,
        canvasBg = appTheme.canvasBg,
        cardBg = appTheme.cardBg,
        textPrimary = appTheme.textPrimary,
        textSecondary = appTheme.textSecondary,
        primaryColor = appTheme.primaryColor,
        primaryDarkColor = appTheme.primaryDarkColor,
        navBg = appTheme.navBg
    )

    val layoutDirection = if (appLanguage == AppLanguage.AR) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalThemeStyle provides themeStyle,
        LocalAppLanguage provides appLanguage,
        LocalLayoutDirection provides layoutDirection
    ) {
        MaterialTheme(
            colorScheme = appTheme.toColorScheme(),
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    appTheme: AppTheme = AppTheme.CYBER_NEON,
    appLanguage: AppLanguage = AppLanguage.AR,
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AutoKeepTheme(appTheme = appTheme, appLanguage = appLanguage, content = content)
}


