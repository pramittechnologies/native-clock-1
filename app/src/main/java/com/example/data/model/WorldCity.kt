package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "world_cities")
data class WorldCity(
    @PrimaryKey
    val id: String, // e.g. "America/New_York"
    val cityName: String,
    val countryName: String,
    val timeZoneId: String,
    val orderIndex: Int = 0
)
