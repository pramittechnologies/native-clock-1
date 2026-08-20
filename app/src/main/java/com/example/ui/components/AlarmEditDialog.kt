package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Alarm
import com.example.ui.theme.GeoActiveBadge
import com.example.ui.theme.GeoActiveCard
import com.example.ui.theme.GeoError
import com.example.ui.theme.GeoInactiveBadge
import com.example.ui.theme.GeoInactiveCard
import com.example.ui.theme.GeoOnBackground
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlarmEditDialog(
    alarm: Alarm?,
    is24Hour: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (Alarm) -> Unit,
    onDelete: ((Alarm) -> Unit)? = null
) {
    var hour by remember { mutableIntStateOf(alarm?.hour ?: 7) }
    var minute by remember { mutableIntStateOf(alarm?.minute ?: 0) }
    var label by remember { mutableStateOf(alarm?.label ?: "Alarm") }
    var daysMask by remember { mutableIntStateOf(alarm?.daysMask ?: 62) }
    var isVibrate by remember { mutableStateOf(alarm?.isVibrate ?: true) }
    var soundName by remember { mutableStateOf(alarm?.soundName ?: "Digital Radar") }
    var snoozeMinutes by remember { mutableIntStateOf(alarm?.snoozeMinutes ?: 5) }

    val isPm = hour >= 12
    val displayHour = when {
        is24Hour -> hour
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("alarm_edit_dialog"),
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = if (alarm == null) "New Alarm" else "Edit Alarm",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                color = GeoOnBackground
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Time Wheel Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = GeoInactiveCard
                    ),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Hour Picker Column
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = { hour = (hour + 1) % 24 },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase Hour", tint = GeoPrimary)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                        .clickable { hour = (hour + 1) % 24 },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (is24Hour) String.format("%02d", hour) else displayHour.toString(),
                                        style = MaterialTheme.typography.displayMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            letterSpacing = (-1.0).sp
                                        ),
                                        color = GeoPrimary
                                    )
                                }
                                IconButton(
                                    onClick = { hour = if (hour == 0) 23 else hour - 1 },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease Hour", tint = GeoPrimary)
                                }
                            }

                            Text(
                                text = ":",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp),
                                color = GeoOnBackground
                            )

                            // Minute Picker Column
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = { minute = (minute + 1) % 60 },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase Minute", tint = GeoPrimary)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                        .clickable { minute = (minute + 5) % 60 },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = String.format("%02d", minute),
                                        style = MaterialTheme.typography.displayMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            letterSpacing = (-1.0).sp
                                        ),
                                        color = GeoPrimary
                                    )
                                }
                                IconButton(
                                    onClick = { minute = if (minute == 0) 59 else minute - 1 },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease Minute", tint = GeoPrimary)
                                }
                            }

                            // AM/PM Switcher
                            if (!is24Hour) {
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (!isPm) GeoPrimaryContainer else Color.White
                                            )
                                            .clickable {
                                                if (isPm) hour -= 12
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "AM",
                                            fontWeight = FontWeight.Bold,
                                            color = if (!isPm) GeoPrimary else GeoOnBackground.copy(alpha = 0.5f)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (isPm) GeoPrimaryContainer else Color.White
                                            )
                                            .clickable {
                                                if (!isPm) hour = (hour + 12) % 24
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "PM",
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPm) GeoPrimary else GeoOnBackground.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Label Input
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Alarm Label") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("alarm_label_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Repeat Days Section
                Text(
                    text = "Repeat Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = GeoOnBackground,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf(
                        "Once" to 0,
                        "Weekdays" to 62,
                        "Weekends" to 65,
                        "Daily" to 127
                    )
                    presets.forEach { (name, mask) ->
                        val isSelected = daysMask == mask
                        FilterChip(
                            selected = isSelected,
                            onClick = { daysMask = mask },
                            label = { Text(name, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GeoPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = GeoInactiveCard,
                                labelColor = GeoOnBackground
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 7 Days Circles
                val dayLabels = listOf("S", "M", "T", "W", "T", "F", "S")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 0..6) {
                        val isSelected = (daysMask and (1 shl i)) != 0
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) GeoPrimary else GeoInactiveCard
                                )
                                .clickable {
                                    daysMask = daysMask xor (1 shl i)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayLabels[i],
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else GeoOnBackground.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vibrate Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Vibration,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Vibration", style = MaterialTheme.typography.bodyLarge, color = GeoOnBackground)
                    }
                    Switch(
                        checked = isVibrate,
                        onCheckedChange = { isVibrate = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GeoPrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = GeoOutline,
                            uncheckedBorderColor = Color.Transparent
                        )
                    )
                }

                // Sound Selector
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Sound", style = MaterialTheme.typography.bodyLarge, color = GeoOnBackground)
                    }

                    val sounds = listOf("Digital Radar", "Cosmic Chime", "Zen Bowl", "Gentle Breeze")
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GeoInactiveCard)
                            .clickable {
                                val nextIndex = (sounds.indexOf(soundName) + 1) % sounds.size
                                soundName = sounds[nextIndex]
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = soundName,
                            style = MaterialTheme.typography.labelMedium,
                            color = GeoPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Snooze selector
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Snooze", style = MaterialTheme.typography.bodyLarge, color = GeoOnBackground)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(5, 10, 15).forEach { mins ->
                            val isSelected = snoozeMinutes == mins
                            FilterChip(
                                selected = isSelected,
                                onClick = { snoozeMinutes = mins },
                                label = { Text("${mins}m") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GeoPrimaryContainer,
                                    selectedLabelColor = GeoPrimary,
                                    containerColor = GeoInactiveCard,
                                    labelColor = GeoOnBackground
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = null
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalAlarm = Alarm(
                        id = alarm?.id ?: 0L,
                        hour = hour,
                        minute = minute,
                        label = label.ifBlank { "Alarm" },
                        isEnabled = true,
                        daysMask = daysMask,
                        isVibrate = isVibrate,
                        soundName = soundName,
                        snoozeMinutes = snoozeMinutes
                    )
                    onSave(finalAlarm)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GeoPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_alarm_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                if (alarm != null && onDelete != null) {
                    TextButton(
                        onClick = {
                            onDelete(alarm)
                            onDismiss()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = GeoError),
                        modifier = Modifier.testTag("delete_alarm_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete")
                    }
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("cancel_alarm_button")
                ) {
                    Text("Cancel", color = GeoOnBackground.copy(alpha = 0.7f))
                }
            }
        }
    )
}
