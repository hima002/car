@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.hima.alwarsha.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hima.alwarsha.data.model.ChatMessage
import com.hima.alwarsha.data.model.ChatRole
import com.hima.alwarsha.data.model.ObdCatalog
import com.hima.alwarsha.data.model.ObdCode
import com.hima.alwarsha.ui.theme.LocalThemeStyle
import com.hima.alwarsha.viewmodel.CarViewModel
import com.hima.alwarsha.viewmodel.DiagnosticsViewModel

@Composable
fun DiagnosticsScreen(
    carViewModel: CarViewModel,
    onBack: () -> Unit,
    diagnosticsViewModel: DiagnosticsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val themeStyle = LocalThemeStyle.current
    val car by carViewModel.selectedCar.collectAsState()
    val messages by diagnosticsViewModel.messages.collectAsState()
    val isSending by diagnosticsViewModel.isSending.collectAsState()

    var obdExpanded by remember { mutableStateOf(false) }
    var obdQuery by remember { mutableStateOf("") }
    var inputText by remember { mutableStateOf("") }
    var attachedImage by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) attachedImage = bitmap
    }

    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("دليل الأعطال والمساعد الذكي", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary) },
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
        Column(Modifier.fillMaxSize().padding(innerPadding)) {

            // Quick OBD-code reference, collapsed by default so the chat gets the space.
            Column(Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { obdExpanded = !obdExpanded }.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("دليل أكواد OBD السريع", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary)
                    Icon(
                        if (obdExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = themeStyle.textSecondary
                    )
                }
                AnimatedVisibility(visible = obdExpanded) {
                    Column {
                        OutlinedTextField(
                            value = obdQuery,
                            onValueChange = { obdQuery = it },
                            label = { Text("ابحث بالكود أو الوصف") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        val results = remember(obdQuery) { ObdCatalog.search(obdQuery) }
                        LazyColumn(modifier = Modifier.height(220.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(results) { code -> ObdCodeCompactCard(code) }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            // Chat area
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(Modifier.height(4.dp)) }
                if (messages.isEmpty()) {
                    item {
                        Text(
                            "اسأل عن أي عرض بيحصل مع عربيتك، أو ارفق صورة للعطل وهقولك السبب المحتمل والحل.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = themeStyle.textSecondary
                        )
                    }
                }
                items(messages) { message -> ChatBubble(message) }
                if (isSending) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = themeStyle.primaryColor, strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("بيفكر...", style = MaterialTheme.typography.bodySmall, color = themeStyle.textSecondary)
                        }
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }

            // Attached image preview
            attachedImage?.let { bitmap ->
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(onClick = { attachedImage = null }) {
                        Icon(Icons.Default.Close, contentDescription = "إزالة الصورة", tint = themeStyle.textSecondary)
                    }
                }
            }

            // Input row
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { cameraLauncher.launch(null) }) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "تصوير العطل", tint = themeStyle.primaryColor)
                }
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("اكتب سؤالك...") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    if (inputText.isNotBlank() || attachedImage != null) {
                        diagnosticsViewModel.sendMessage(car, inputText, attachedImage)
                        inputText = ""
                        attachedImage = null
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "إرسال", tint = themeStyle.primaryColor)
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val themeStyle = LocalThemeStyle.current
    val isUser = message.role == ChatRole.USER
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
        Card(
            modifier = Modifier.padding(vertical = 2.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) themeStyle.primaryColor.copy(alpha = 0.18f) else themeStyle.cardBg
            ),
            border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
        ) {
            Column(Modifier.padding(12.dp)) {
                message.imageBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(6.dp))
                }
                if (message.text.isNotBlank()) {
                    Text(message.text, color = themeStyle.textPrimary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun ObdCodeCompactCard(code: ObdCode) {
    val themeStyle = LocalThemeStyle.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Column(Modifier.padding(10.dp)) {
            Text("${code.code} — ${code.titleAr}", fontWeight = FontWeight.Bold, color = themeStyle.primaryColor, style = MaterialTheme.typography.labelLarge)
            Text(code.solutionAr, style = MaterialTheme.typography.bodySmall, color = themeStyle.textSecondary)
        }
    }
}
