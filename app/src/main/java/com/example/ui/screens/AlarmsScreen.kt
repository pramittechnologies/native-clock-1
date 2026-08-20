package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Alarm
import com.example.ui.components.AlarmEditDialog
import com.example.ui.theme.GeoActiveBadge
import com.example.ui.theme.GeoActiveCard
import com.example.ui.theme.GeoFabBackground
import com.example.ui.theme.GeoFabIcon
import com.example.ui.theme.GeoInactiveBadge
import com.example.ui.theme.GeoInactiveCard
import com.example.ui.theme.GeoOnBackground
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.viewmodel.ClockViewModel
import com.example.util.TimeFormatters

@Composable
fun AlarmsScreen(
    viewModel: ClockViewModel,
    alarms: List<Alarm>,
    is24Hour: Boolean,
    modifier: Modifier = Modifier
) {
    var editingAlarm by remember { mutableStateOf<Alarm?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Find next active alarm
    val nextAlarm = remember(alarms) {
        alarms.filter { it.isEnabled }.minByOrNull {
            it.hour * 60 + it.minute
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("alarms_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Next Alarm Info Header
            if (nextAlarm != null) {
                item {
                    NextAlarmBanner(
                        nextAlarm = nextAlarm,
                        is24Hour = is24Hour
                    )
                }
            }

            if (alarms.isEmpty()) {
                item {
                    EmptyAlarmsView(onAddClick = { showAddDialog = true })
                }
            } else {
                items(alarms, key = { it.id }) { alarm ->
                    AlarmCard(
                        alarm = alarm,
                        is24Hour = is24Hour,
                        onToggle = { viewModel.toggleAlarmEnabled(alarm) },
                        onClick = { editingAlarm = alarm },
                        onTest = { viewModel.testAlarm(alarm) }
                    )
                }
            }
        }

        // Geometric Balance Floating Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 24.dp)
                .size(56.dp)
                .testTag("add_alarm_fab"),
            containerColor = GeoFabBackground,
            contentColor = GeoFabIcon,
            shape = RoundedCornerShape(18.dp) // rounded-2xl
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Alarm", modifier = Modifier.size(30.dp))
        }
    }

    // Add Alarm Dialog
    if (showAddDialog) {
        AlarmEditDialog(
            alarm = null,
            is24Hour = is24Hour,
            onDismiss = { showAddDialog = false },
            onSave = { newAlarm ->
                viewModel.saveAlarm(newAlarm)
            }
        )
    }

    // Edit Alarm Dialog
    editingAlarm?.let { alarm ->
        AlarmEditDialog(
            alarm = alarm,
            is24Hour = is24Hour,
            onDismiss = { editingAlarm = null },
            onSave = { updatedAlarm ->
                viewModel.saveAlarm(updatedAlarm)
            },
            onDelete = { alarmToDelete ->
                viewModel.deleteAlarm(alarmToDelete)
            }
        )
    }
}

@Composable
fun NextAlarmBanner(
    nextAlarm: Alarm,
    is24Hour: Boolean
) {
    val (timeStr, amPm) = TimeFormatters.formatAlarmTime(nextAlarm.hour, nextAlarm.minute, is24Hour)
    val timeUntil = TimeFormatters.calculateTimeUntilAlarm(nextAlarm.hour, nextAlarm.minute, nextAlarm.daysMask)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("next_alarm_banner"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = GeoPrimary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AlarmOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Next Alarm in $timeUntil",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "$timeStr $amPm • ${nextAlarm.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
fun AlarmCard(
    alarm: Alarm,
    is24Hour: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onTest: () -> Unit
) {
    val (timeStr, amPm) = TimeFormatters.formatAlarmTime(alarm.hour, alarm.minute, is24Hour)
    val isEnabled = alarm.isEnabled

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("alarm_card_${alarm.id}"),
        shape = RoundedCornerShape(28.dp), // rounded-3xl
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) GeoActiveCard else GeoInactiveCard
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp) // p-6
        ) {
            // Top row: Large Digital Time + Custom Geometric Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = (-1.5).sp
                        ),
                        color = if (isEnabled) GeoOnBackground else GeoOnBackground.copy(alpha = 0.5f)
                    )
                    if (amPm.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = amPm,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (isEnabled) GeoOnBackground.copy(alpha = 0.8f) else GeoOnBackground.copy(alpha = 0.4f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                // Geometric Balance Switch
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = GeoPrimary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = GeoOutline,
                        uncheckedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.testTag("alarm_switch_${alarm.id}")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom row: Repeat Schedule & Category Tag Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = alarm.getDaysSummary(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = if (isEnabled) GeoOnBackground else GeoOnBackground.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Geometric Badge Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp)) // rounded-lg
                            .background(
                                if (isEnabled) GeoActiveBadge else GeoInactiveBadge.copy(alpha = 0.7f)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = alarm.label.uppercase().ifBlank { "ALARM" },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = if (isEnabled) GeoPrimary else GeoOnBackground.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Quick test preview button
                    IconButton(
                        onClick = onTest,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("test_alarm_btn_${alarm.id}")
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Test Alarm",
                            tint = if (isEnabled) GeoPrimary else GeoOnBackground.copy(alpha = 0.35f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyAlarmsView(onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        colors = CardDefaults.cardColors(
            containerColor = GeoInactiveCard
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Alarm,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = GeoPrimary.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Alarms Set",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = GeoOnBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap the + button to create an alarm with custom schedules.",
                style = MaterialTheme.typography.bodyMedium,
                color = GeoOnBackground.copy(alpha = 0.65f)
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick = onAddClick,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create Alarm")
            }
        }
    }
}
