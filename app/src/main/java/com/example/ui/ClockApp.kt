package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ActiveAlertOverlay
import com.example.ui.screens.AlarmsScreen
import com.example.ui.screens.StopwatchScreen
import com.example.ui.screens.TimerScreen
import com.example.ui.screens.WorldClockScreen
import com.example.ui.theme.GeoActiveCard
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoNavBackground
import com.example.ui.theme.GeoOnBackground
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.viewmodel.ClockViewModel

sealed class ClockTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    object Alarms : ClockTab("Alarm", Icons.Filled.Alarm, Icons.Outlined.Alarm, "tab_alarms")
    object WorldClock : ClockTab("World", Icons.Filled.Public, Icons.Outlined.Public, "tab_world_clock")
    object Timer : ClockTab("Timer", Icons.Filled.Timer, Icons.Outlined.Timer, "tab_timer")
    object Stopwatch : ClockTab("Stopwatch", Icons.Filled.HourglassBottom, Icons.Outlined.HourglassBottom, "tab_stopwatch")
}

val clockTabs = listOf(
    ClockTab.Alarms,
    ClockTab.WorldClock,
    ClockTab.Timer,
    ClockTab.Stopwatch
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockApp(
    viewModel: ClockViewModel = viewModel()
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    // Observers
    val is24Hour by viewModel.is24HourFormat.collectAsStateWithLifecycle()
    val localTime by viewModel.localTime.collectAsStateWithLifecycle()
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()
    val worldCities by viewModel.savedWorldCities.collectAsStateWithLifecycle()

    // Stopwatch observers
    val stopwatchElapsed by viewModel.stopwatchElapsedMillis.collectAsStateWithLifecycle()
    val stopwatchRunning by viewModel.stopwatchRunning.collectAsStateWithLifecycle()
    val laps by viewModel.laps.collectAsStateWithLifecycle()

    // Timer observers
    val timerTotalSecs by viewModel.timerTotalSeconds.collectAsStateWithLifecycle()
    val timerRemainingSecs by viewModel.timerRemainingSeconds.collectAsStateWithLifecycle()
    val timerRunning by viewModel.timerRunning.collectAsStateWithLifecycle()
    val timerLabel by viewModel.timerLabel.collectAsStateWithLifecycle()

    // Active alert
    val activeAlert by viewModel.activeAlert.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = GeoBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = clockTabs[selectedTabIndex].title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = GeoOnBackground,
                        fontWeight = FontWeight.Normal
                    )
                },
                actions = {
                    FilterChip(
                        selected = is24Hour,
                        onClick = { viewModel.toggle24HourFormat() },
                        label = {
                            Text(
                                text = if (is24Hour) "24h" else "12h",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GeoPrimaryContainer,
                            selectedLabelColor = GeoPrimary,
                            containerColor = GeoNavBackground,
                            labelColor = GeoOnBackground
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = is24Hour,
                            borderColor = GeoOutlineVariant,
                            selectedBorderColor = GeoPrimaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .testTag("time_format_toggle")
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GeoBackground
                )
            )
        },
        bottomBar = {
            // Geometric Balance Navigation Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GeoNavBackground)
                    .navigationBarsPadding()
                    .testTag("clock_navigation_bar")
            ) {
                Divider(color = GeoOutlineVariant, thickness = 1.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    clockTabs.forEachIndexed { index, tab ->
                        val isSelected = selectedTabIndex == index

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    selectedTabIndex = index
                                }
                                .padding(vertical = 4.dp)
                                .testTag(tab.tag)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 64.dp, height = 32.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) GeoActiveCard else Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title,
                                    tint = if (isSelected) GeoPrimary else GeoOnBackground.copy(alpha = 0.65f),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) GeoPrimary else GeoOnBackground.copy(alpha = 0.65f)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "clock_tab_content"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> AlarmsScreen(
                        viewModel = viewModel,
                        alarms = alarms,
                        is24Hour = is24Hour
                    )
                    1 -> WorldClockScreen(
                        viewModel = viewModel,
                        localTime = localTime,
                        worldCities = worldCities,
                        is24Hour = is24Hour
                    )
                    2 -> TimerScreen(
                        viewModel = viewModel,
                        totalSeconds = timerTotalSecs,
                        remainingSeconds = timerRemainingSecs,
                        isRunning = timerRunning,
                        timerLabel = timerLabel
                    )
                    3 -> StopwatchScreen(
                        viewModel = viewModel,
                        elapsedMillis = stopwatchElapsed,
                        isRunning = stopwatchRunning,
                        laps = laps
                    )
                }
            }
        }
    }

    // Active Alarm or Timer alert popup overlay
    activeAlert?.let { alert ->
        ActiveAlertOverlay(
            alert = alert,
            onDismiss = { viewModel.dismissActiveAlert() },
            onSnooze = { mins -> viewModel.snoozeActiveAlert(mins) }
        )
    }
}
