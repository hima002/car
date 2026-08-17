package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.StatusLevel
import com.example.ui.theme.LocalThemeStyle
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.testTag

@Composable
fun HealthGaugeCard(
    healthScore: Int,
    statusTextAr: String,
    overallLevel: StatusLevel,
    nextDueText: String,
    onTriggerNotification: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val themeStyle = LocalThemeStyle.current
    val scoreColor = when (overallLevel) {
        StatusLevel.GREEN -> StatusGreen
        StatusLevel.YELLOW -> StatusYellow
        StatusLevel.RED -> StatusRed
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = themeStyle.cardShape,
        colors = CardDefaults.cardColors(containerColor = themeStyle.cardBg),
        border = androidx.compose.foundation.BorderStroke(themeStyle.cardBorderWidth, themeStyle.cardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Gauge Canvas
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(105.dp)
                ) {
                    CircularHealthIndicator(
                        score = healthScore,
                        scoreColor = scoreColor,
                        size = 105.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$healthScore%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = themeStyle.textPrimary
                        )
                        Text(
                            text = "صحة السيارة",
                            style = MaterialTheme.typography.labelSmall,
                            color = themeStyle.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Text Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Traffic Light Status Chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(themeStyle.chipShape)
                            .background(scoreColor.copy(alpha = 0.15f))
                            .border(1.5.dp, scoreColor.copy(alpha = 0.4f), themeStyle.chipShape)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        // Traffic light trio icons
                        Text(
                            text = when (overallLevel) {
                                StatusLevel.GREEN -> "🟢 حالة آمنة وممتازة"
                                StatusLevel.YELLOW -> "🟡 تنبيه - صيانة قريبة"
                                StatusLevel.RED -> "🔴 صيانة متأخرة حارجة"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = statusTextAr,
                        style = MaterialTheme.typography.bodyMedium,
                        color = themeStyle.textPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (nextDueText.isNotEmpty()) {
                        Text(
                            text = "أقرب صيانة: $nextDueText",
                            style = MaterialTheme.typography.bodySmall,
                            color = themeStyle.primaryColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Visual Traffic Light Status Bar
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(themeStyle.chipShape)
                    .background(themeStyle.cardBorderColor.copy(alpha = 0.3f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مؤشر الجاهزية 🚦:",
                    style = MaterialTheme.typography.labelSmall,
                    color = themeStyle.textSecondary,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Green Light Indicator
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (overallLevel == StatusLevel.GREEN) StatusGreen else StatusGreen.copy(alpha = 0.2f))
                            .border(1.dp, StatusGreen, CircleShape)
                    )
                    // Yellow Light Indicator
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (overallLevel == StatusLevel.YELLOW) StatusYellow else StatusYellow.copy(alpha = 0.2f))
                            .border(1.dp, StatusYellow, CircleShape)
                    )
                    // Red Light Indicator
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (overallLevel == StatusLevel.RED) StatusRed else StatusRed.copy(alpha = 0.2f))
                            .border(1.dp, StatusRed, CircleShape)
                    )
                }
            }

            if (onTriggerNotification != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onTriggerNotification,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_push_notification_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primaryColor.copy(alpha = 0.15f)),
                    shape = themeStyle.buttonShape
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = themeStyle.primaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "فحص وإرسال تنبيه الصيانة (Push Notification) 🔔",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = themeStyle.primaryColor
                    )
                }
            }
        }
    }
}

@Composable
fun CircularHealthIndicator(
    score: Int,
    scoreColor: Color,
    size: Dp = 100.dp,
    strokeWidth: Dp = 10.dp
) {
    val themeStyle = LocalThemeStyle.current
    val animatedProgress by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "HealthProgress"
    )

    Canvas(modifier = Modifier.size(size)) {
        val sweepAngle = animatedProgress * 360f

        // Background Track
        drawArc(
            color = themeStyle.cardBorderColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )

        // Progress Track
        drawArc(
            color = scoreColor,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}

