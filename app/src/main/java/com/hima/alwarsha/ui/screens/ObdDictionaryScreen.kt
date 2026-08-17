@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.hima.alwarsha.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hima.alwarsha.data.model.ObdCatalog
import com.hima.alwarsha.data.model.ObdCode
import com.hima.alwarsha.ui.theme.LocalThemeStyle

@Composable
fun ObdDictionaryScreen(onBack: () -> Unit) {
    val themeStyle = LocalThemeStyle.current
    var query by remember { mutableStateOf("") }
    val results = remember(query) { ObdCatalog.search(query) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("دليل أعطال OBD-II", fontWeight = FontWeight.Bold, color = themeStyle.textPrimary) },
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
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("ابحث بالكود أو الوصف") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(results) { code -> ObdCodeCard(code) }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun ObdCodeCard(code: ObdCode) {
    val themeStyle = LocalThemeStyle.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("${code.code} — ${code.titleAr}", fontWeight = FontWeight.Bold, color = themeStyle.primaryColor)
            Spacer(Modifier.height(6.dp))
            Text("الأسباب المحتملة: ${code.causesAr}", style = MaterialTheme.typography.bodySmall, color = themeStyle.textPrimary)
            Spacer(Modifier.height(4.dp))
            Text("الحل المقترح: ${code.solutionAr}", style = MaterialTheme.typography.bodySmall, color = themeStyle.textSecondary)
        }
    }
}
