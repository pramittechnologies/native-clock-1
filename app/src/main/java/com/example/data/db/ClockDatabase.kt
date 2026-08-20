package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.WorldCitiesProvider
import com.example.data.model.Alarm
import com.example.data.model.WorldCity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Alarm::class, WorldCity::class],
    version = 1,
    exportSchema = false
)
abstract class ClockDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    abstract fun worldCityDao(): WorldCityDao

    companion object {
        @Volatile
        private var INSTANCE: ClockDatabase? = null

        fun getDatabase(context: Context, coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)): ClockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClockDatabase::class.java,
                    "clock_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial default alarms and world cities
                        coroutineScope.launch(Dispatchers.IO) {
                            INSTANCE?.let { database ->
                                // Default Alarms
                                database.alarmDao().insertAlarm(
                                    Alarm(
                                        hour = 7,
                                        minute = 0,
                                        label = "Morning Alarm",
                                        isEnabled = true,
                                        daysMask = 62 // Weekdays
                                    )
                                )
                                database.alarmDao().insertAlarm(
                                    Alarm(
                                        hour = 8,
                                        minute = 30,
                                        label = "Weekend Wake Up",
                                        isEnabled = false,
                                        daysMask = 65 // Weekends
                                    )
                                )
                                // Default World Cities
                                database.worldCityDao().insertCities(WorldCitiesProvider.defaultInitialCities)
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
