package com.hima.alwarsha.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hima.alwarsha.data.model.Workshop
import com.hima.alwarsha.ui.theme.LocalThemeStyle
import com.hima.alwarsha.ui.theme.StatusYellow
import com.hima.alwarsha.viewmodel.WorkshopViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopFinderScreen(
    carBrand: String?,
    hasLocationPermission: Boolean,
    onRequestLocationPermission: () -> Unit,
    onBack: () -> Unit,
    viewModel: WorkshopViewModel = viewModel()
) {
    val themeStyle = LocalThemeStyle.current
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) viewModel.searchNearby(carBrand)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("أفضل ورش قريبة منك", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                        if (carBrand != null) {
                            Text(
                                "مرشحة لعربية $carBrand",
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
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                !hasLocationPermission -> PermissionPrompt(onRequestLocationPermission)
                uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = themeStyle.primaryColor)
                }
                uiState.errorMessageAr != null && uiState.workshops.isEmpty() -> ErrorMessage(uiState.errorMessageAr!!)
                else -> LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item { Spacer(Modifier.height(8.dp)) }
                    items(uiState.workshops) { workshop ->
                        WorkshopCard(workshop) {
                            val uri = Uri.parse(
                                "https://www.google.com/maps/search/?api=1&query=${workshop.lat},${workshop.lng}&query_place_id=${workshop.placeId}"
                            )
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun PermissionPrompt(onRequest: () -> Unit) {
    val themeStyle = LocalThemeStyle.current
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.LocationOn, contentDescription = null, tint = themeStyle.primaryColor, modifier = Modifier.height(48.dp))
        Spacer(Modifier.height(12.dp))
        Text(
            "محتاج صلاحية الموقع عشان أقدر أدور على أفضل ورش قريبة منك",
            color = themeStyle.textPrimary,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRequest, colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor)) {
            Text("تفعيل صلاحية الموقع")
        }
    }
}

@Composable
private fun ErrorMessage(message: String) {
    val themeStyle = LocalThemeStyle.current
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(message, color = themeStyle.textSecondary, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun WorkshopCard(workshop: Workshop, onClick: () -> Unit) {
    val themeStyle = LocalThemeStyle.current
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    workshop.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = themeStyle.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "${(workshop.distanceKm * 10).roundToInt() / 10.0} كم",
                    style = MaterialTheme.typography.labelMedium,
                    color = themeStyle.primaryColor
                )
            }
            Spacer(Modifier.height(4.dp))
            if (workshop.address.isNotBlank()) {
                Text(workshop.address, style = MaterialTheme.typography.bodySmall, color = themeStyle.textSecondary)
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (workshop.rating != null) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = StatusYellow, modifier = Modifier.height(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${workshop.rating} (${workshop.userRatingsTotal} تقييم)",
                        style = MaterialTheme.typography.labelSmall,
                        color = themeStyle.textPrimary
                    )
                } else {
                    Text("بدون تقييمات كافية", style = MaterialTheme.typography.labelSmall, color = themeStyle.textSecondary)
                }
                if (!workshop.isOpen) {
                    Spacer(Modifier.width(8.dp))
                    Text("مغلقة حاليًا", style = MaterialTheme.typography.labelSmall, color = themeStyle.textSecondary)
                }
            }
        }
    }
}
