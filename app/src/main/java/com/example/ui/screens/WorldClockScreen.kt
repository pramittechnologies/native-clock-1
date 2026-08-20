package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.WorldCity
import com.example.ui.components.AddCityDialog
import com.example.ui.components.AnalogClockView
import com.example.ui.theme.GeoActiveBadge
import com.example.ui.theme.GeoActiveCard
import com.example.ui.theme.GeoFabBackground
import com.example.ui.theme.GeoFabIcon
import com.example.ui.theme.GeoInactiveBadge
import com.example.ui.theme.GeoInactiveCard
import com.example.ui.theme.GeoOnBackground
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoSecondary
import com.example.ui.theme.GeoWarning
import com.example.ui.viewmodel.ClockViewModel
import com.example.ui.viewmodel.CurrentLocalTime
import com.example.util.TimeFormatters

@Composable
fun WorldClockScreen(
    viewModel: ClockViewModel,
    localTime: CurrentLocalTime,
    worldCities: List<WorldCity>,
    is24Hour: Boolean,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("world_clock_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Local Time Hero Card
            item {
                LocalTimeHeroCard(
                    localTime = localTime,
                    is24Hour = is24Hour
                )
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "World Cities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = GeoOnBackground
                    )
                    Text(
                        text = "${worldCities.size} cities",
                        style = MaterialTheme.typography.labelSmall,
                        color = GeoSecondary
                    )
                }
            }

            if (worldCities.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = GeoInactiveCard),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No world cities added yet.\nTap the + button to search and add cities.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GeoOnBackground.copy(alpha = 0.65f)
                            )
                        }
                    }
                }
            } else {
                items(worldCities, key = { it.id }) { city ->
                    WorldCityCard(
                        city = city,
                        is24Hour = is24Hour,
                        onDelete = { viewModel.removeWorldCity(city) }
                    )
                }
            }
        }

        // Geometric Balance FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 24.dp)
                .size(56.dp)
                .testTag("add_city_fab"),
            containerColor = GeoFabBackground,
            contentColor = GeoFabIcon,
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add City", modifier = Modifier.size(30.dp))
        }
    }

    if (showAddDialog) {
        AddCityDialog(
            existingCityIds = worldCities.map { it.id }.toSet(),
            is24Hour = is24Hour,
            onDismiss = { showAddDialog = false },
            onAddCity = { city ->
                viewModel.addWorldCity(city)
            }
        )
    }
}

@Composable
fun LocalTimeHeroCard(
    localTime: CurrentLocalTime,
    is24Hour: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("local_time_hero_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = GeoActiveCard
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = GeoPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Current Time • ${localTime.timeZoneName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = GeoPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Analog Clock Canvas
            AnalogClockView(
                hour = localTime.hour,
                minute = localTime.minute,
                second = localTime.second,
                size = 180.dp,
                showSeconds = true,
                isDaytime = localTime.isDaytime,
                accentColor = GeoPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Digital Time Display
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = localTime.formattedTime,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = (-1.5).sp
                    ),
                    color = GeoOnBackground
                )
                if (localTime.amPm.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = localTime.amPm,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = GeoOnBackground.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = localTime.formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = GeoOnBackground.copy(alpha = 0.65f)
            )
        }
    }
}

@Composable
fun WorldCityCard(
    city: WorldCity,
    is24Hour: Boolean,
    onDelete: () -> Unit
) {
    val cityTimeInfo = TimeFormatters.getCityTimeInfo(city.timeZoneId, is24Hour)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("world_city_card_${city.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = GeoInactiveCard
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Mini Analog Clock
            AnalogClockView(
                hour = cityTimeInfo.hour,
                minute = cityTimeInfo.minute,
                second = cityTimeInfo.second,
                size = 52.dp,
                showSeconds = false,
                isDaytime = cityTimeInfo.isDaytime,
                accentColor = GeoPrimary
            )

            Spacer(modifier = Modifier.width(14.dp))

            // City info & relative time
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = city.cityName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = GeoOnBackground
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (cityTimeInfo.isDaytime) Icons.Default.WbSunny else Icons.Default.NightlightRound,
                        contentDescription = if (cityTimeInfo.isDaytime) "Day" else "Night",
                        tint = if (cityTimeInfo.isDaytime) GeoWarning else GeoSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "${city.countryName} • ${cityTimeInfo.timeDifferenceString}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GeoOnBackground.copy(alpha = 0.65f)
                )
            }

            // Digital time & Delete Action
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = cityTimeInfo.timeString,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = (-0.5).sp
                            ),
                            color = GeoOnBackground
                        )
                        if (cityTimeInfo.amPm.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = cityTimeInfo.amPm,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = GeoOnBackground.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_city_${city.id}")
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete City",
                        tint = GeoOnBackground.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
