package com.hima.alwarsha.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.hima.alwarsha.ui.theme.LocalThemeStyle
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Free alternative to a paid Places API search: hands off to the Google Maps app itself with a
 * pre-filled, location-biased search query. No API key, no Google Cloud billing — Maps shows its
 * own real results and ratings, we just launch it with the right query.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopFinderScreen(
    carBrand: String?,
    hasLocationPermission: Boolean,
    onRequestLocationPermission: () -> Unit,
    onBack: () -> Unit
) {
    val themeStyle = LocalThemeStyle.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isSearching by remember { mutableStateOf(false) }

    val searchQuery = if (carBrand != null) "ورشة صيانة سيارات $carBrand" else "ورشة صيانة سيارات"

    fun openMapsSearch() {
        isSearching = true
        scope.launch {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val location = runCatching { fusedLocationClient.lastLocation.await() }.getOrNull()

            val uri = if (location != null) {
                Uri.parse("geo:${location.latitude},${location.longitude}?q=${Uri.encode(searchQuery)}")
            } else {
                Uri.parse("geo:0,0?q=${Uri.encode(searchQuery)}")
            }

            val mapsIntent = Intent(Intent.ACTION_VIEW, uri).setPackage("com.google.android.apps.maps")
            try {
                context.startActivity(mapsIntent)
            } catch (e: ActivityNotFoundException) {
                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
            isSearching = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("أفضل ورش قريبة منك", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                        if (carBrand != null) {
                            Text(
                                "بحث جاهز لعربية $carBrand",
                                style = MaterialTheme.typography.labelSmall,
                                color = themeStyle.textSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = themeStyle.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = themeStyle.canvasBg)
            )
        },
        containerColor = themeStyle.canvasBg
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Map, contentDescription = null, tint = themeStyle.primaryColor, modifier = Modifier.height(56.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                "هنفتحلك تطبيق خرائط جوجل ببحث جاهز عن \"$searchQuery\" حوالين موقعك الحالي — هتشوف نتايج جوجل الحقيقية بتقييماتها.",
                color = themeStyle.textPrimary,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))

            if (!hasLocationPermission) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = themeStyle.textSecondary, modifier = Modifier.height(32.dp))
                Spacer(Modifier.height(8.dp))
                Text(
                    "من غير صلاحية الموقع، هيفتح البحث عام بدون تحديد مكانك.",
                    color = themeStyle.textSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onRequestLocationPermission,
                    colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("تفعيل صلاحية الموقع أولًا")
                }
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = { openMapsSearch() },
                colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor),
                enabled = !isSearching,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSearching) "جاري الفتح..." else "افتح خرائط جوجل وابحث الآن")
            }
        }
    }
}
