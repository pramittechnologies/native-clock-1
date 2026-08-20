package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GeoActiveBadge
import com.example.ui.theme.GeoActiveCard
import com.example.ui.theme.GeoError
import com.example.ui.theme.GeoInactiveCard
import com.example.ui.theme.GeoOnBackground
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSecondary
import com.example.ui.theme.GeoSuccess
import com.example.ui.viewmodel.ClockViewModel
import com.example.ui.viewmodel.LapRecord
import com.example.util.TimeFormatters

@Composable
fun StopwatchScreen(
    viewModel: ClockViewModel,
    elapsedMillis: Long,
    isRunning: Boolean,
    laps: List<LapRecord>,
    modifier: Modifier = Modifier
) {
    val fastestLapNum = remember(laps) {
        if (laps.size >= 2) laps.minByOrNull { it.lapTimeMillis }?.lapNumber else null
    }
    val slowestLapNum = remember(laps) {
        if (laps.size >= 2) laps.maxByOrNull { it.lapTimeMillis }?.lapNumber else null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("stopwatch_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Geometric Dial & Time Counter
        Box(
            modifier = Modifier
                .size(260.dp)
                .testTag("stopwatch_dial_container"),
            contentAlignment = Alignment.Center
        ) {
            val trackColor = GeoInactiveCard
            val sweepColor = if (isRunning) GeoPrimary else GeoSecondary
            val secondSweepProgress = (elapsedMillis % 60000) / 60000f

            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f - 12.dp.toPx()
                val center = Offset(size.width / 2f, size.height / 2f)

                // Background track
                drawCircle(
                    color = trackColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 8.dp.toPx())
                )

                // Active progress arc
                drawArc(
                    color = sweepColor,
                    startAngle = -90f,
                    sweepAngle = secondSweepProgress * 360f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Inner Time Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val formattedTime = TimeFormatters.formatStopwatchTime(elapsedMillis)

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = (-1.0).sp
                    ),
                    color = GeoOnBackground,
                    textAlign = TextAlign.Center
                )

                if (laps.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val currentLapTime = elapsedMillis - (laps.firstOrNull()?.overallTimeMillis ?: 0L)
                    Text(
                        text = "Lap ${laps.size + 1}: ${TimeFormatters.formatStopwatchTime(if (isRunning) currentLapTime.coerceAtLeast(0) else 0)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = GeoSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Control Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lap / Reset Button (Left)
            if (isRunning) {
                FilledTonalButton(
                    onClick = { viewModel.lapStopwatch() },
                    modifier = Modifier
                        .size(76.dp)
                        .testTag("lap_button"),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = GeoPrimaryContainer,
                        contentColor = GeoPrimary
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Flag, contentDescription = "Lap", modifier = Modifier.size(22.dp))
                        Text("Lap", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                FilledTonalButton(
                    onClick = { viewModel.resetStopwatch() },
                    enabled = elapsedMillis > 0,
                    modifier = Modifier
                        .size(76.dp)
                        .testTag("reset_stopwatch_button"),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = GeoInactiveCard,
                        contentColor = GeoOnBackground,
                        disabledContainerColor = GeoInactiveCard.copy(alpha = 0.5f),
                        disabledContentColor = GeoOnBackground.copy(alpha = 0.3f)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(22.dp))
                        Text("Reset", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Start / Pause Button (Right)
            Button(
                onClick = {
                    if (isRunning) viewModel.pauseStopwatch() else viewModel.startStopwatch()
                },
                modifier = Modifier
                    .size(84.dp)
                    .testTag("start_pause_stopwatch_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) GeoError else GeoPrimary,
                    contentColor = Color.White
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = if (isRunning) "Pause" else "Start",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Laps List Card
        if (laps.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GeoActiveCard
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "LAP",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimary,
                            modifier = Modifier.width(60.dp)
                        )
                        Text(
                            text = "SPLIT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "TOTAL",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimary,
                            textAlign = TextAlign.End,
                            modifier = Modifier.width(90.dp)
                        )
                    }

                    Divider(color = GeoOutlineVariant, thickness = 1.dp)

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(laps, key = { it.lapNumber }) { lap ->
                            val isFastest = lap.lapNumber == fastestLapNum
                            val isSlowest = lap.lapNumber == slowestLapNum

                            val textColor = when {
                                isFastest -> GeoSuccess
                                isSlowest -> GeoError
                                else -> GeoOnBackground
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.width(60.dp)
                                ) {
                                    Text(
                                        text = String.format("%02d", lap.lapNumber),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    if (isFastest) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(GeoSuccess)
                                        )
                                    } else if (isSlowest) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(GeoError)
                                        )
                                    }
                                }

                                Text(
                                    text = "+${TimeFormatters.formatStopwatchTime(lap.lapTimeMillis)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = TimeFormatters.formatStopwatchTime(lap.overallTimeMillis),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = GeoOnBackground.copy(alpha = 0.7f),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(90.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Press Start, then Lap to record splits.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GeoOnBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
