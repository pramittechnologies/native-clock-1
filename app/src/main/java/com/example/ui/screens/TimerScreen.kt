package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.GeoInactiveBadge
import com.example.ui.theme.GeoInactiveCard
import com.example.ui.theme.GeoOnBackground
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSecondary
import com.example.ui.viewmodel.ClockViewModel
import com.example.util.TimeFormatters

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimerScreen(
    viewModel: ClockViewModel,
    totalSeconds: Long,
    remainingSeconds: Long,
    isRunning: Boolean,
    timerLabel: String,
    modifier: Modifier = Modifier
) {
    var showCustomTimerDialog by remember { mutableStateOf(false) }

    val progress = if (totalSeconds > 0) {
        (remainingSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 200),
        label = "timer_progress"
    )

    val presets = listOf(
        "1m" to 60L,
        "3m" to 180L,
        "5m" to 300L,
        "10m" to 600L,
        "15m" to 900L,
        "25m" to 1500L,
        "30m" to 1800L,
        "1h" to 3600L
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("timer_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Timer Dial
        Box(
            modifier = Modifier
                .size(260.dp)
                .clickable {
                    if (!isRunning) {
                        showCustomTimerDialog = true
                    }
                }
                .testTag("timer_dial_container"),
            contentAlignment = Alignment.Center
        ) {
            val trackColor = GeoInactiveCard
            val ringColor = when {
                remainingSeconds <= 10 && isRunning -> GeoError
                isRunning -> GeoPrimary
                else -> GeoSecondary
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f - 14.dp.toPx()
                val center = Offset(size.width / 2f, size.height / 2f)

                // Background track circle
                drawCircle(
                    color = trackColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 10.dp.toPx())
                )

                // Active progress countdown sweep arc
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Center Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showCustomTimerDialog = true }
                ) {
                    Text(
                        text = timerLabel.ifBlank { "Timer" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = GeoOnBackground.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit timer",
                        tint = GeoPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = TimeFormatters.formatTimerDuration(remainingSeconds),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = (-1.5).sp
                    ),
                    color = GeoOnBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                val percentLeft = (progress * 100).toInt()
                Text(
                    text = "$percentLeft% remaining",
                    style = MaterialTheme.typography.labelSmall,
                    color = GeoSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Timer Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset Button
            FilledTonalButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier
                    .size(76.dp)
                    .testTag("reset_timer_button"),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = GeoInactiveCard,
                    contentColor = GeoOnBackground
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(22.dp))
                    Text("Reset", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Start / Pause Button
            Button(
                onClick = {
                    if (isRunning) viewModel.pauseTimer() else viewModel.startTimer()
                },
                modifier = Modifier
                    .size(84.dp)
                    .testTag("start_pause_timer_button"),
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

            // +1:00 Quick Add Button
            FilledTonalButton(
                onClick = { viewModel.addOneMinuteToTimer() },
                modifier = Modifier
                    .size(76.dp)
                    .testTag("add_minute_button"),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = GeoPrimaryContainer,
                    contentColor = GeoPrimary
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("+1:00", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GeoPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Presets Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = GeoActiveCard
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Presets",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = GeoPrimary
                    )
                    TextButton(onClick = { showCustomTimerDialog = true }) {
                        Text("Custom", color = GeoPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { (label, durationSecs) ->
                        val isSelected = totalSeconds == durationSecs
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.setPresetTimer(durationSecs, label.substringBefore(" "))
                            },
                            label = {
                                Text(
                                    label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GeoPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White.copy(alpha = 0.6f),
                                labelColor = GeoPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = null
                        )
                    }
                }
            }
        }
    }

    // Custom Timer Duration Dialog
    if (showCustomTimerDialog) {
        CustomTimerDialog(
            currentTotalSeconds = totalSeconds,
            currentLabel = timerLabel,
            onDismiss = { showCustomTimerDialog = false },
            onSetTimer = { h, m, s, lbl ->
                viewModel.setTimerDuration(h, m, s, lbl)
                viewModel.startTimer()
            }
        )
    }
}

@Composable
fun CustomTimerDialog(
    currentTotalSeconds: Long,
    currentLabel: String,
    onDismiss: () -> Unit,
    onSetTimer: (Int, Int, Int, String) -> Unit
) {
    val initialHours = (currentTotalSeconds / 3600).toInt()
    val initialMinutes = ((currentTotalSeconds % 3600) / 60).toInt()
    val initialSeconds = (currentTotalSeconds % 60).toInt()

    var hours by remember { mutableIntStateOf(initialHours) }
    var minutes by remember { mutableIntStateOf(if (initialHours == 0 && initialMinutes == 0 && initialSeconds == 0) 5 else initialMinutes) }
    var seconds by remember { mutableIntStateOf(initialSeconds) }
    var label by remember { mutableStateOf(currentLabel.ifBlank { "Timer" }) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("custom_timer_dialog"),
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = "Set Timer",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                color = GeoOnBackground
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(GeoInactiveCard)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeColumnPicker(
                        label = "hours",
                        value = hours,
                        onIncrease = { hours = (hours + 1) % 100 },
                        onDecrease = { hours = if (hours == 0) 99 else hours - 1 }
                    )

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = GeoOnBackground
                    )

                    TimeColumnPicker(
                        label = "min",
                        value = minutes,
                        onIncrease = { minutes = (minutes + 1) % 60 },
                        onDecrease = { minutes = if (minutes == 0) 59 else minutes - 1 }
                    )

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = GeoOnBackground
                    )

                    TimeColumnPicker(
                        label = "sec",
                        value = seconds,
                        onIncrease = { seconds = (seconds + 1) % 60 },
                        onDecrease = { seconds = if (seconds == 0) 59 else seconds - 1 }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Timer Label") },
                    placeholder = { Text("e.g. Tea, Workout, Cooking") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (hours > 0 || minutes > 0 || seconds > 0) {
                        onSetTimer(hours, minutes, seconds, label.ifBlank { "Timer" })
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GeoPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("start_custom_timer_button")
            ) {
                Text("Start Timer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = GeoOnBackground.copy(alpha = 0.7f))
            }
        }
    )
}

@Composable
fun TimeColumnPicker(
    label: String,
    value: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase $label", tint = GeoPrimary)
        }

        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format("%02d", value),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = GeoPrimary
            )
        }

        IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease $label", tint = GeoPrimary)
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = GeoOnBackground.copy(alpha = 0.6f)
        )
    }
}
