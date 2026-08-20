package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.WorldCitiesProvider
import com.example.data.model.WorldCity
import com.example.ui.theme.GeoActiveCard
import com.example.ui.theme.GeoInactiveCard
import com.example.ui.theme.GeoOnBackground
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSecondary
import com.example.util.TimeFormatters

@Composable
fun AddCityDialog(
    existingCityIds: Set<String>,
    is24Hour: Boolean = false,
    onDismiss: () -> Unit,
    onAddCity: (WorldCity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("All") }

    val regions = listOf("All", "Americas", "Europe", "Asia", "Africa", "Australia", "Pacific")

    val filteredCities = remember(searchQuery, selectedRegion, existingCityIds) {
        WorldCitiesProvider.allSupportedCities.filter { city ->
            val matchesSearch = searchQuery.isBlank() ||
                    city.cityName.contains(searchQuery, ignoreCase = true) ||
                    city.countryName.contains(searchQuery, ignoreCase = true)

            val matchesRegion = when (selectedRegion) {
                "All" -> true
                "Americas" -> city.timeZoneId.startsWith("America")
                "Europe" -> city.timeZoneId.startsWith("Europe") || city.timeZoneId.startsWith("Atlantic")
                "Asia" -> city.timeZoneId.startsWith("Asia")
                "Africa" -> city.timeZoneId.startsWith("Africa")
                "Australia" -> city.timeZoneId.startsWith("Australia")
                "Pacific" -> city.timeZoneId.startsWith("Pacific")
                else -> true
            }

            matchesSearch && matchesRegion
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .testTag("add_city_dialog"),
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Public,
                        contentDescription = null,
                        tint = GeoPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add World Clock",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Medium,
                        color = GeoOnBackground
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = GeoOnBackground)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = GeoPrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    placeholder = { Text("Search city or country...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("city_search_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Region filter chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(regions) { region ->
                        val isSelected = selectedRegion == region
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedRegion = region },
                            label = { Text(region, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GeoPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = GeoInactiveCard,
                                labelColor = GeoOnBackground
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cities List
                if (filteredCities.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No cities found matching \"$searchQuery\"",
                            color = GeoOnBackground.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredCities, key = { it.id }) { city ->
                            val isAlreadyAdded = existingCityIds.contains(city.id)
                            val cityTimeInfo = TimeFormatters.getCityTimeInfo(city.timeZoneId, is24Hour)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isAlreadyAdded) {
                                        onAddCity(city)
                                    }
                                    .testTag("city_item_${city.id}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isAlreadyAdded)
                                        GeoInactiveCard.copy(alpha = 0.5f)
                                    else
                                        GeoInactiveCard
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = city.cityName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isAlreadyAdded)
                                                GeoOnBackground.copy(alpha = 0.4f)
                                            else
                                                GeoOnBackground
                                        )
                                        Text(
                                            text = "${city.countryName} • ${cityTimeInfo.timeDifferenceString}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = GeoOnBackground.copy(alpha = 0.6f)
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Column(horizontalAlignment = Alignment.End) {
                                            Row(verticalAlignment = Alignment.Bottom) {
                                                Text(
                                                    text = cityTimeInfo.timeString,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (isAlreadyAdded)
                                                        GeoOnBackground.copy(alpha = 0.4f)
                                                    else
                                                        GeoOnBackground
                                                )
                                                if (cityTimeInfo.amPm.isNotEmpty()) {
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = cityTimeInfo.amPm,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = GeoPrimary
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        if (isAlreadyAdded) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(GeoOutline.copy(alpha = 0.3f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "Added",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = GeoOnBackground.copy(alpha = 0.5f)
                                                )
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(GeoPrimaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Add,
                                                    contentDescription = "Add City",
                                                    modifier = Modifier.size(18.dp),
                                                    tint = GeoPrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = GeoPrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}
