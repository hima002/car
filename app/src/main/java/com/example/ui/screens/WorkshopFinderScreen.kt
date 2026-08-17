package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Workshop
import com.example.ui.theme.LocalThemeStyle
import com.example.ui.theme.StatusGreen
import com.example.viewmodel.CarViewModel

@Composable
fun WorkshopFinderScreen(
    viewModel: CarViewModel,
    onBack: () -> Unit
) {
    val themeStyle = LocalThemeStyle.current
    val context = LocalContext.current
    val workshops = viewModel.getWorkshops()
    val selectedCar by viewModel.selectedCar.collectAsState()

    val carBrand = selectedCar?.brand ?: ""
    val carModel = selectedCar?.model ?: ""

    // Default filter to MY_CAR if a car is registered, otherwise ALL
    var selectedBrandFilter by remember(selectedCar) { 
        mutableStateOf(if (selectedCar != null && carBrand.isNotBlank()) "MY_CAR" else "ALL") 
    }

    // Filter workshops strictly based on selected brand / registered car
    val displayedWorkshops = when (selectedBrandFilter) {
        "MY_CAR" -> {
            workshops.filter { w ->
                isBrandMatched(carBrand, w.specialtyBrands)
            }
        }
        "ALL" -> workshops
        else -> {
            workshops.filter { w ->
                isBrandMatched(selectedBrandFilter, w.specialtyBrands)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeStyle.canvasBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = themeStyle.textPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "دليل ومراكز الصيانة المعتمدة 🛠️",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = themeStyle.textPrimary
                    )
                    Text(
                        text = if (selectedCar != null) "مراكز ترشيح ذكية تناسب سيارتك $carBrand $carModel" else "اختر أفضل مركز معتمد لسيارتك",
                        style = MaterialTheme.typography.bodySmall,
                        color = themeStyle.primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Tabs Row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (selectedCar != null && carBrand.isNotBlank()) {
                    item {
                        FilterChip(
                            selected = selectedBrandFilter == "MY_CAR",
                            onClick = { selectedBrandFilter = "MY_CAR" },
                            label = { Text("🎯 ورش سيارتك ($carBrand $carModel)", fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AutoGold,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                item {
                    FilterChip(
                        selected = selectedBrandFilter == "ALL",
                        onClick = { selectedBrandFilter = "ALL" },
                        label = { Text("🏬 جميع الورش والدليل الشامل") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AutoGold,
                            selectedLabelColor = Color.Black
                        )
                    )
                }

                val otherBrands = listOf(
                    "Hyundai" to "هيونداي Hyundai 🚙",
                    "Toyota" to "تويوتا Toyota 🏎️",
                    "Chery" to "شيري Chery 🚘",
                    "Nissan" to "نيسان Nissan 🚗",
                    "Kia" to "كيا Kia 🚙",
                    "European" to "ألماني / أوروبي 🏎️"
                )

                items(otherBrands) { (code, label) ->
                    FilterChip(
                        selected = selectedBrandFilter == code,
                        onClick = { selectedBrandFilter = code },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AutoGold,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // AUTO FILTER BANNER
                if (selectedBrandFilter == "MY_CAR" && selectedCar != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = AutoSurfaceDark),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AutoGold.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(AutoGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = AutoGold,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "تم جلب الورش المعتمدة لسيارتك تلقائياً 🎯",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AutoGold
                                    )
                                    Text(
                                        text = "السيارة المسجلة: $carBrand $carModel • تم العثور على (${displayedWorkshops.size}) مراكز متخصصة",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                }

                items(displayedWorkshops) { workshop ->
                    val matchesCar = selectedCar != null && isBrandMatched(carBrand, workshop.specialtyBrands)
                    WorkshopCard(
                        workshop = workshop,
                        isCarMatched = matchesCar,
                        carBrandName = carBrand,
                        onCall = {
                            Toast.makeText(
                                context,
                                "جاري الاتصال بـ ${workshop.nameAr} (${workshop.phoneNumber})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }

                if (displayedWorkshops.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "لا توجد ورش مطابقة للفلتر المحدد حالياً.",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun WorkshopCard(
    workshop: Workshop,
    isCarMatched: Boolean = false,
    carBrandName: String = "",
    onCall: () -> Unit
) {
    val themeStyle = LocalThemeStyle.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("workshop_item_card_${workshop.id}"),
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = androidx.compose.foundation.BorderStroke(
            themeStyle.cardBorderWidth,
            if (isCarMatched) themeStyle.primaryColor else themeStyle.cardBorderColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isCarMatched && carBrandName.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(themeStyle.primaryColor.copy(alpha = 0.15f), themeStyle.chipShape)
                            .border(1.dp, themeStyle.primaryColor, themeStyle.chipShape)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = null,
                                tint = themeStyle.primaryColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "موصى به لـ $carBrandName 🚘",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = themeStyle.primaryColor
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = workshop.nameAr,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = themeStyle.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                if (workshop.isVerifiedPartner) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Partner",
                        tint = StatusGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = themeStyle.primaryColor)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${workshop.rating} (${workshop.reviewCount} تقييم)",
                    style = MaterialTheme.typography.bodySmall,
                    color = themeStyle.primaryColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = themeStyle.textSecondary)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = workshop.areaAr,
                    style = MaterialTheme.typography.bodySmall,
                    color = themeStyle.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "التخصصات: ${workshop.categories.joinToString(" • ")}",
                style = MaterialTheme.typography.bodySmall,
                color = themeStyle.primaryColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            val context = LocalContext.current

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Google Maps Button
                OutlinedButton(
                    onClick = {
                        val searchQuery = if (workshop.mapQuery.isNotBlank()) workshop.mapQuery else "${workshop.nameAr} ${workshop.areaAr}"
                        val encodedQuery = Uri.encode(searchQuery)
                        val geoUri = Uri.parse("geo:${workshop.latitude},${workshop.longitude}?q=$encodedQuery")
                        
                        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            val webIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.google.com/maps/search/?api=1&query=$encodedQuery")
                            )
                            try {
                                context.startActivity(webIntent)
                            } catch (ex: Exception) {
                                Toast.makeText(context, "تعذر فتح خرائط جوجل على جهازك", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("map_workshop_button_${workshop.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = themeStyle.primaryColor),
                    border = androidx.compose.foundation.BorderStroke(1.dp, themeStyle.primaryColor.copy(alpha = 0.6f)),
                    shape = themeStyle.buttonShape
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = themeStyle.primaryColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("خريطة جوجل 🗺️", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }

                // Call / Reserve Button
                Button(
                    onClick = onCall,
                    colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("call_workshop_button_${workshop.id}"),
                    shape = themeStyle.buttonShape
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("اتصل الآن 📞", fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

private fun isBrandMatched(targetBrand: String, specialtyBrands: List<String>): Boolean {
    if (targetBrand.isBlank()) return false
    val targetNorm = targetBrand.trim().lowercase()

    return specialtyBrands.any { specialty ->
        val specNorm = specialty.trim().lowercase()

        // "All Brands" or "جميع الماركات" covers every vehicle
        if (specNorm == "all brands" || specNorm == "all" || specNorm == "جميع الماركات" || specNorm.contains("جميع")) {
            return@any true
        }

        // Direct equality or substring matches
        if (specNorm.equals(targetNorm, ignoreCase = true) ||
            specNorm.contains(targetNorm) ||
            targetNorm.contains(specNorm)
        ) {
            return@any true
        }

        // Arabic <-> English brand translations
        val isHyundaiMatch = (targetNorm.contains("hyundai") || targetNorm.contains("هيونداي")) &&
                (specNorm.contains("hyundai") || specNorm.contains("هيونداي"))

        val isToyotaMatch = (targetNorm.contains("toyota") || targetNorm.contains("تويوتا")) &&
                (specNorm.contains("toyota") || specNorm.contains("تويوتا"))

        val isCheryMatch = (targetNorm.contains("chery") || targetNorm.contains("شيري")) &&
                (specNorm.contains("chery") || specNorm.contains("شيري"))

        val isNissanMatch = (targetNorm.contains("nissan") || targetNorm.contains("نيسان")) &&
                (specNorm.contains("nissan") || specNorm.contains("نيسان"))

        val isKiaMatch = (targetNorm.contains("kia") || targetNorm.contains("كيا")) &&
                (specNorm.contains("kia") || specNorm.contains("كيا"))

        val isMgMatch = (targetNorm.contains("mg") || targetNorm.contains("إم جي") || targetNorm.contains("ام جي")) &&
                (specNorm.contains("mg") || specNorm.contains("إم جي") || specNorm.contains("ام جي"))

        val isMitsubishiMatch = (targetNorm.contains("mitsubishi") || targetNorm.contains("ميتسوبيشي")) &&
                (specNorm.contains("mitsubishi") || specNorm.contains("ميتسوبيشي"))

        val isEuropeanMatch = (targetNorm.contains("european") || targetNorm.contains("ألماني") || targetNorm.contains("أوروبي") || targetNorm.contains("bmw") || targetNorm.contains("mercedes") || targetNorm.contains("audi") || targetNorm.contains("volkswagen") || targetNorm.contains("skoda") || targetNorm.contains("peugeot") || targetNorm.contains("fiat") || targetNorm.contains("renault")) &&
                (specNorm.contains("european") || specNorm.contains("bmw") || specNorm.contains("mercedes") || specNorm.contains("audi") || specNorm.contains("volkswagen") || specNorm.contains("skoda") || specNorm.contains("peugeot") || specNorm.contains("fiat") || specNorm.contains("renault") || specNorm.contains("ألماني") || specNorm.contains("أوروبي"))

        isHyundaiMatch || isToyotaMatch || isCheryMatch || isNissanMatch || isKiaMatch || isMgMatch || isMitsubishiMatch || isEuropeanMatch
    }
}

