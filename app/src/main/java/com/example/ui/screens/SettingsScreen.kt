package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.AppTheme
import com.example.ui.theme.LocalAppLanguage
import com.example.ui.theme.LocalThemeStyle
import com.example.viewmodel.CarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CarViewModel,
    onBack: () -> Unit,
    onNavigateToTracking: () -> Unit
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val themeStyle = LocalThemeStyle.current
    val lang = LocalAppLanguage.current
    val isAr = lang == AppLanguage.AR

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = themeStyle.primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "إعدادات التطبيق والتصميم ⚙️" else "App Settings & Themes ⚙️",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = themeStyle.textPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isAr) "رجوع" else "Back",
                            tint = themeStyle.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeStyle.canvasBg
                )
            )
        },
        containerColor = themeStyle.canvasBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 0. AUTOMATIC DRIVING TRACKING ENTRY POINT
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToTracking() }
                        .testTag("settings_tracking_entry"),
                    shape = RoundedCornerShape(themeStyle.cardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
                    border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GpsFixed,
                                contentDescription = null,
                                tint = themeStyle.primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "التتبع التلقائي للقيادة (GPS)" else "Automatic Driving Tracking (GPS)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = themeStyle.textPrimary
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = themeStyle.textSecondary
                        )
                    }
                }
            }

            // 1. LIVE PREVIEW CARD OF SELECTED THEME
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isAr) "🔍 معاينة حية للتصميم المختار:" else "🔍 Live Theme Preview:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = themeStyle.primaryColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(currentTheme.cardCornerRadius),
                        colors = CardDefaults.cardColors(containerColor = currentTheme.cardBg),
                        border = BorderStroke(currentTheme.cardBorderWidth, currentTheme.cardBorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isAr) currentTheme.nameAr else currentTheme.nameEn,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = currentTheme.textPrimary
                                    )
                                    Text(
                                        text = if (isAr) currentTheme.descAr else currentTheme.descEn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = currentTheme.textSecondary
                                    )
                                }

                                Surface(
                                    color = currentTheme.primaryColor,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = if (isAr) "نشط الأن ✨" else "Active ✨",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primaryColor),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = if (isAr) "زر رئيسي" else "Primary Button",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }

                                Surface(
                                    color = currentTheme.canvasBg,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, currentTheme.cardBorderColor),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = if (isAr) "عنصر ثانوي" else "Secondary Card",
                                            color = currentTheme.textSecondary,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. LANGUAGE SELECTOR SECTION
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(themeStyle.cardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
                    border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = themeStyle.primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "لغة الواجهة (App Language):" else "Interface Language:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = themeStyle.textPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = isAr,
                                onClick = { viewModel.setLanguage(AppLanguage.AR) },
                                label = { Text("🇸🇦 العربية (RTL)", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.weight(1f).testTag("settings_lang_ar"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = themeStyle.primaryColor,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            FilterChip(
                                selected = !isAr,
                                onClick = { viewModel.setLanguage(AppLanguage.EN) },
                                label = { Text("🇬🇧 English (LTR)", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.weight(1f).testTag("settings_lang_en"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = themeStyle.primaryColor,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            // 3. THEME SELECTION HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = themeStyle.primaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "معرض الثيمات والتصاميم (12 ثيم):" else "Theme Gallery (12 Unique Themes):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = themeStyle.textPrimary
                    )
                }
            }

            // 4. THEME CARDS LIST
            items(AppTheme.values()) { theme ->
                val isSelected = theme == currentTheme
                val themeName = if (isAr) theme.nameAr else theme.nameEn
                val themeBadge = if (isAr) theme.badgeAr else theme.badgeEn
                val themeDesc = if (isAr) theme.descAr else theme.descEn

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setTheme(theme) }
                        .testTag("settings_theme_card_${theme.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = theme.canvasBg
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.5.dp else theme.cardBorderWidth,
                        color = if (isSelected) theme.primaryColor else theme.cardBorderColor
                    ),
                    shape = RoundedCornerShape(theme.cardCornerRadius)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Palette Color Bubbles
                            Row(
                                horizontalArrangement = Arrangement.spacedBy((-6).dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(theme.primaryColor)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(theme.cardBg)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(theme.cardBorderColor)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = themeName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.textPrimary
                                )
                                Text(
                                    text = themeDesc,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = theme.textSecondary,
                                    maxLines = 2
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Selection Badge / Indicator
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(theme.primaryColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            Surface(
                                color = theme.primaryColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = themeBadge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = theme.primaryColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
