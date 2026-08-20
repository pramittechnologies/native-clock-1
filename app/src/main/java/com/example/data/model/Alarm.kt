package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int, // 0..23
    val minute: Int, // 0..59
    val label: String = "Alarm",
    val isEnabled: Boolean = true,
    val daysMask: Int = 0, // Bitmask: bit 0 = Sun, bit 1 = Mon, ..., bit 6 = Sat. 0 = Once
    val isVibrate: Boolean = true,
    val soundName: String = "Digital Radar",
    val snoozeMinutes: Int = 5
) {
    fun isDaySelected(dayIndex: Int): Boolean { // 0 = Sun, 1 = Mon ... 6 = Sat
        return (daysMask and (1 shl dayIndex)) != 0
    }

    fun getDaysSummary(): String {
        if (daysMask == 0) return "Once"
        if (daysMask == 127) return "Every day" // 1+2+4+8+16+32+64
        if (daysMask == 62) return "Weekdays" // Mon-Fri (1<<1 to 1<<5)
        if (daysMask == 65) return "Weekends" // Sun (1<<0) + Sat (1<<6)

        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val activeDays = mutableListOf<String>()
        for (i in 0..6) {
            if (isDaySelected(i)) {
                activeDays.add(dayNames[i])
            }
        }
        return activeDays.joinToString(", ")
    }
}
