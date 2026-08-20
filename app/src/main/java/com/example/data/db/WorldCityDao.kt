package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.WorldCity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorldCityDao {
    @Query("SELECT * FROM world_cities ORDER BY orderIndex ASC")
    fun getAllCities(): Flow<List<WorldCity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: WorldCity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<WorldCity>)

    @Delete
    suspend fun deleteCity(city: WorldCity)

    @Query("DELETE FROM world_cities WHERE id = :id")
    suspend fun deleteCityById(id: String)
}
