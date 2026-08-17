package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AppLanguage {
    AR,
    EN
}

enum class AppTheme(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val badgeAr: String,
    val badgeEn: String,
    val descAr: String,
    val descEn: String,
    val isDark: Boolean,
    val canvasBg: Color,
    val cardBg: Color,
    val cardBorderColor: Color,
    val cardBorderWidth: Dp,
    val cardCornerRadius: Dp,
    val primaryColor: Color,
    val primaryDarkColor: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val navBg: Color,
    val indicatorBg: Color
) {
    EDITORIAL(
        id = "editorial",
        nameAr = "الكلاسيكي التحريري",
        nameEn = "Classic Editorial",
        badgeAr = "ورقي ناعم",
        badgeEn = "Classic Editorial",
        descAr = "تصميم صحفي كلاسيكي بخلفية دافئة وحواف ناعمة بلمسة فاخرة",
        descEn = "Warm paper background with sleek border lines and elegant typography",
        isDark = true,
        canvasBg = Color(0xFF0F172A),
        cardBg = Color(0xFF1E293B),
        cardBorderColor = Color(0xFF334155),
        cardBorderWidth = 1.dp,
        cardCornerRadius = 16.dp,
        primaryColor = Color(0xFFF59E0B),
        primaryDarkColor = Color(0xFFB45309),
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFF94A3B8),
        navBg = Color(0xFF020617),
        indicatorBg = Color(0xFF334155)
    ),

    CYBER_NEON(
        id = "cyber_neon",
        nameAr = "السايبر المضيء 2099",
        nameEn = "Cyber Neon 2099",
        badgeAr = "نيون مشع",
        badgeEn = "Cyber Neon",
        descAr = "خلفية فضائية مظلمة بحدود نيون متوهجة وألوان سيبرانية مستقبليّة",
        descEn = "Deep cyber black canvas with glowing electric cyan & magenta borders",
        isDark = true,
        canvasBg = Color(0xFF090A0F),
        cardBg = Color(0xFF121520),
        cardBorderColor = Color(0xFF00F0FF),
        cardBorderWidth = 1.5.dp,
        cardCornerRadius = 8.dp,
        primaryColor = Color(0xFF00F0FF),
        primaryDarkColor = Color(0xFF008B99),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFF80A0C0),
        navBg = Color(0xFF05060A),
        indicatorBg = Color(0xFF1E2A3A)
    ),

    CARBON_RACING(
        id = "carbon_racing",
        nameAr = "ألياف الكربون السباقية",
        nameEn = "Carbon Racing Spec",
        badgeAr = "حواف حادة رياضية",
        badgeEn = "Aggressive Racing",
        descAr = "مظهر سيارات سباق فورمولا بطابع ألياف الكربون وأحمر سريع وحواف حادة",
        descEn = "Racing spec carbon dark style with intense crimson red accents and sharp corners",
        isDark = true,
        canvasBg = Color(0xFF121214),
        cardBg = Color(0xFF1C1C20),
        cardBorderColor = Color(0xFFFF2A3B),
        cardBorderWidth = 1.5.dp,
        cardCornerRadius = 4.dp,
        primaryColor = Color(0xFFFF2A3B),
        primaryDarkColor = Color(0xFF990011),
        textPrimary = Color(0xFFF0F0F2),
        textSecondary = Color(0xFFA0A0A8),
        navBg = Color(0xFF0A0A0C),
        indicatorBg = Color(0xFF2E1A20)
    ),

    GLASS_LUXE(
        id = "glass_luxe",
        nameAr = "الزجاجي العصري (Glassmorphism)",
        nameEn = "Glassmorphism Luxe",
        badgeAr = "زجاجي شبه شفاف",
        badgeEn = "Frosted Glass",
        descAr = "أسطح زجاجية شبه شفافة مع حدود مضيئة ناعمة ومنحنيات عصرية",
        descEn = "Translucent frosted surfaces with ice-white glowing outlines & soft curves",
        isDark = true,
        canvasBg = Color(0xFF141727),
        cardBg = Color(0xFF20253D),
        cardBorderColor = Color(0xFF434D75),
        cardBorderWidth = 1.dp,
        cardCornerRadius = 22.dp,
        primaryColor = Color(0xFF818CF8),
        primaryDarkColor = Color(0xFF4F46E5),
        textPrimary = Color(0xFFF1F5F9),
        textSecondary = Color(0xFF94A3B8),
        navBg = Color(0xFF0D101D),
        indicatorBg = Color(0xFF313858)
    ),

    LUXURY_GOLD(
        id = "luxury_gold",
        nameAr = "الفخامة الملكية ذهبي وأونيكس",
        nameEn = "Luxury Gold & Onyx",
        badgeAr = "ذهب ملكي مصقول",
        badgeEn = "Polished Gold",
        descAr = "أسود أونيكس مع إطارات وبصمات ذهبية فاخرة تناسب سيارات VIP",
        descEn = "Onyx black background paired with rich metallic champagne gold accents",
        isDark = true,
        canvasBg = Color(0xFF0A0A0C),
        cardBg = Color(0xFF151518),
        cardBorderColor = Color(0xFFD4AF37),
        cardBorderWidth = 1.dp,
        cardCornerRadius = 14.dp,
        primaryColor = Color(0xFFE5C158),
        primaryDarkColor = Color(0xFF8A6D1C),
        textPrimary = Color(0xFFFAFAFA),
        textSecondary = Color(0xFFB0A890),
        navBg = Color(0xFF050506),
        indicatorBg = Color(0xFF2A2412)
    ),

    NORDIC_CLEAN(
        id = "nordic_clean",
        nameAr = "النوردي الهادئ (فاتح)",
        nameEn = "Nordic Clean (Light)",
        badgeAr = "دائري سوبر فائق",
        badgeEn = "Pill Ultra Soft",
        descAr = "ثيم فاتح هادئ بخلفية ثلجية وزوايا دائرية فائقة النعومة مع لمسات نعناع وأزرق",
        descEn = "Minimalist light theme with super-soft rounded pills and clean icy hues",
        isDark = false,
        canvasBg = Color(0xFFF4F6F9),
        cardBg = Color(0xFFFFFFFF),
        cardBorderColor = Color(0xFFE2E8F0),
        cardBorderWidth = 1.dp,
        cardCornerRadius = 28.dp,
        primaryColor = Color(0xFF0284C7),
        primaryDarkColor = Color(0xFF0369A1),
        textPrimary = Color(0xFF0F172A),
        textSecondary = Color(0xFF64748B),
        navBg = Color(0xFFFFFFFF),
        indicatorBg = Color(0xFFE0F2FE)
    ),

    RETRO_VINTAGE(
        id = "retro_vintage",
        nameAr = "الريترو السبعيني دافئ",
        nameEn = "Retro 70s Vintage",
        badgeAr = "بطاقات كلاسيكية",
        badgeEn = "Retro Badges",
        descAr = "ألوان ريترو دافئة كلاسيكية، كريمي وعنبر وبترولي مع حدود عريضة بارزة",
        descEn = "Warm parchment cream background with bold terracotta orange & forest teal borders",
        isDark = false,
        canvasBg = Color(0xFFF7EFE2),
        cardBg = Color(0xFFFFF9EE),
        cardBorderColor = Color(0xFFD96B27),
        cardBorderWidth = 2.dp,
        cardCornerRadius = 12.dp,
        primaryColor = Color(0xFFD96B27),
        primaryDarkColor = Color(0xFF9E420C),
        textPrimary = Color(0xFF2C221E),
        textSecondary = Color(0xFF7A685D),
        navBg = Color(0xFFEFE4D2),
        indicatorBg = Color(0xFFFCE3D0)
    ),

    DARK_STEALTH(
        id = "dark_stealth",
        nameAr = "التخفي المظلم OLED",
        nameEn = "OLED Dark Stealth",
        badgeAr = "أسود مطلق",
        badgeEn = "Pure Stealth",
        descAr = "أسود OLED مطلق وموفر للطاقة مع تباين عالي بلمسات فضية وزمردية",
        descEn = "Pitch black OLED surface designed for night driving and maximum focus",
        isDark = true,
        canvasBg = Color(0xFF000000),
        cardBg = Color(0xFF141416),
        cardBorderColor = Color(0xFF28282C),
        cardBorderWidth = 1.dp,
        cardCornerRadius = 16.dp,
        primaryColor = Color(0xFF10B981),
        primaryDarkColor = Color(0xFF047857),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFF8E8E93),
        navBg = Color(0xFF08080A),
        indicatorBg = Color(0xFF1B2E26)
    ),

    MATERIAL_EXPRESSIVE(
        id = "material_expressive",
        nameAr = "ماتيريال يو الملون",
        nameEn = "Material You Expressive",
        badgeAr = "أشكال حيوية M3",
        badgeEn = "Dynamic Expressive",
        descAr = "تصميم ماتيريال 3 الملون بأشكال كبسولية حيوية وألوان بنفسجية مميزة",
        descEn = "Vibrant Material 3 layout with capsule cards and deep violet accents",
        isDark = false,
        canvasBg = Color(0xFFF6F0FF),
        cardBg = Color(0xFFFFFFFF),
        cardBorderColor = Color(0xFFE8DDFF),
        cardBorderWidth = 1.dp,
        cardCornerRadius = 32.dp,
        primaryColor = Color(0xFF6750A4),
        primaryDarkColor = Color(0xFF4F378B),
        textPrimary = Color(0xFF1D1B20),
        textSecondary = Color(0xFF49454F),
        navBg = Color(0xFFF0E7FF),
        indicatorBg = Color(0xFFE8DEF8)
    ),

    INDUSTRIAL_TACTICAL(
        id = "industrial_tactical",
        nameAr = "الصناعي التكتيكي",
        nameEn = "Industrial Heavy Duty",
        badgeAr = "معدني صناعي",
        badgeEn = "Heavy Duty Grid",
        descAr = "طابع الورش الفنية والمعدات الثقيلة، رمادي صلب مع أصفر التحذير التكتيكي",
        descEn = "Rugged charcoal metal background with hazard yellow accents and technical grid vibe",
        isDark = true,
        canvasBg = Color(0xFF1A1D24),
        cardBg = Color(0xFF232832),
        cardBorderColor = Color(0xFFEEB111),
        cardBorderWidth = 1.5.dp,
        cardCornerRadius = 6.dp,
        primaryColor = Color(0xFFEEB111),
        primaryDarkColor = Color(0xFF9E7300),
        textPrimary = Color(0xFFF2F4F8),
        textSecondary = Color(0xFFA0AAB8),
        navBg = Color(0xFF12141A),
        indicatorBg = Color(0xFF322C1A)
    ),

    OCEANIC_DEEP(
        id = "oceanic_deep",
        nameAr = "الأعماق البحرية",
        nameEn = "Oceanic Deep Blue",
        badgeAr = "أزرق تركوازي",
        badgeEn = "Turquoise Ocean",
        descAr = "خلفية كحلي بحري عميق مع أزرق تركوازي منعش وزوايا انسيابية",
        descEn = "Deep ocean navy with refreshing turquoise blue highlights and smooth curves",
        isDark = true,
        canvasBg = Color(0xFF0B192C),
        cardBg = Color(0xFF1E3E62),
        cardBorderColor = Color(0xFF00E5FF),
        cardBorderWidth = 1.dp,
        cardCornerRadius = 20.dp,
        primaryColor = Color(0xFF00E5FF),
        primaryDarkColor = Color(0xFF008899),
        textPrimary = Color(0xFFF1F5F9),
        textSecondary = Color(0xFF94A3B8),
        navBg = Color(0xFF060E1A),
        indicatorBg = Color(0xFF143048)
    ),

    EMERALD_PRECISION(
        id = "emerald_precision",
        nameAr = "الزمردي الناصع (فاتح)",
        nameEn = "Emerald Precision (Light)",
        badgeAr = "زمردي ملكي",
        badgeEn = "Royal Emerald",
        descAr = "ثيم أبيض ناصع مع زمردي ملكي وخطوط دقيقة يعكس الدقة والجودة",
        descEn = "Pristine white canvas with rich royal emerald green and precision lines",
        isDark = false,
        canvasBg = Color(0xFFFAFAFA),
        cardBg = Color(0xFFFFFFFF),
        cardBorderColor = Color(0xFFD1E7DD),
        cardBorderWidth = 1.dp,
        cardCornerRadius = 16.dp,
        primaryColor = Color(0xFF00875A),
        primaryDarkColor = Color(0xFF005A3C),
        textPrimary = Color(0xFF0F172A),
        textSecondary = Color(0xFF475569),
        navBg = Color(0xFFFFFFFF),
        indicatorBg = Color(0xFFE6F4ED)
    );

    fun toColorScheme(): ColorScheme {
        return if (isDark) {
            darkColorScheme(
                primary = primaryColor,
                onPrimary = if (isDark) Color.Black else Color.White,
                primaryContainer = indicatorBg,
                onPrimaryContainer = primaryColor,
                secondary = primaryColor,
                onSecondary = Color.Black,
                background = canvasBg,
                onBackground = textPrimary,
                surface = cardBg,
                onSurface = textPrimary,
                surfaceVariant = navBg,
                onSurfaceVariant = textSecondary,
                outline = cardBorderColor
            )
        } else {
            lightColorScheme(
                primary = primaryColor,
                onPrimary = Color.White,
                primaryContainer = indicatorBg,
                onPrimaryContainer = primaryDarkColor,
                secondary = primaryColor,
                onSecondary = Color.White,
                background = canvasBg,
                onBackground = textPrimary,
                surface = cardBg,
                onSurface = textPrimary,
                surfaceVariant = navBg,
                onSurfaceVariant = textSecondary,
                outline = cardBorderColor
            )
        }
    }
}

data class ThemeStyle(
    val theme: AppTheme = AppTheme.CYBER_NEON,
    val cardCornerRadius: Dp = AppTheme.CYBER_NEON.cardCornerRadius,
    val cardShape: Shape = RoundedCornerShape(AppTheme.CYBER_NEON.cardCornerRadius),
    val buttonShape: Shape = RoundedCornerShape(if (AppTheme.CYBER_NEON.cardCornerRadius > 20.dp) 50.dp else 8.dp),
    val chipShape: Shape = RoundedCornerShape(if (AppTheme.CYBER_NEON.cardCornerRadius > 20.dp) 50.dp else 8.dp),
    val cardBorderWidth: Dp = AppTheme.CYBER_NEON.cardBorderWidth,
    val cardBorderColor: Color = AppTheme.CYBER_NEON.cardBorderColor,
    val canvasBg: Color = AppTheme.CYBER_NEON.canvasBg,
    val cardBg: Color = AppTheme.CYBER_NEON.cardBg,
    val textPrimary: Color = AppTheme.CYBER_NEON.textPrimary,
    val textSecondary: Color = AppTheme.CYBER_NEON.textSecondary,
    val primaryColor: Color = AppTheme.CYBER_NEON.primaryColor,
    val primaryDarkColor: Color = AppTheme.CYBER_NEON.primaryDarkColor,
    val navBg: Color = AppTheme.CYBER_NEON.navBg
)

val LocalThemeStyle = compositionLocalOf { ThemeStyle() }
val LocalAppLanguage = compositionLocalOf { AppLanguage.AR }
