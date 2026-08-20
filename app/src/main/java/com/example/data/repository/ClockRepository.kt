package com.example.data.repository

import com.example.data.db.AlarmDao
import com.example.data.db.WorldCityDao
import com.example.data.model.Alarm
import com.example.data.model.WorldCity
import kotlinx.coroutines.flow.Flow

class ClockRepository(
    private val alarmDao: AlarmDao,
    private val worldCityDao: WorldCityDao
) {
    // Alarms
    val allAlarms: Flow<List<Alarm>> = alarmDao.getAllAlarms()
    val enabledAlarms: Flow<List<Alarm>> = alarmDao.getEnabledAlarms()

    suspend fun getAlarmById(id: Long): Alarm? = alarmDao.getAlarmById(id)
    suspend fun insertAlarm(alarm: Alarm): Long = alarmDao.insertAlarm(alarm)
    suspend fun updateAlarm(alarm: Alarm) = alarmDao.updateAlarm(alarm)
    suspend fun deleteAlarm(alarm: Alarm) = alarmDao.deleteAlarm(alarm)
    suspend fun deleteAlarmById(id: Long) = alarmDao.deleteAlarmById(id)

    // World Cities
    val allWorldCities: Flow<List<WorldCity>> = worldCityDao.getAllCities()
    suspend fun insertCity(city: WorldCity) = worldCityDao.insertCity(city)
    suspend fun insertCities(cities: List<WorldCity>) = worldCityDao.insertCities(cities)
    suspend fun deleteCity(city: WorldCity) = worldCityDao.deleteCity(city)
    suspend fun deleteCityById(id: String) = worldCityDao.deleteCityById(id)
}
