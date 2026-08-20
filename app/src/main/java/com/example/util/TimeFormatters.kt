package com.example.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeFormatters {

    // Format stopwatch time: "MM:SS.ss" or "HH:MM:SS.ss"
    fun formatStopwatchTime(elapsedMillis: Long): String {
        val hours = (elapsedMillis / 3600000)
        val minutes = (elapsedMillis % 3600000) / 60000
        val seconds = (elapsedMillis % 60000) / 1000
        val hundredths = (elapsedMillis % 1000) / 10

        return if (hours > 0) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d.%02d", hours, minutes, seconds, hundredths)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, hundredths)
        }
    }

    // Format timer seconds: "HH:MM:SS" or "MM:SS"
    fun formatTimerDuration(totalSeconds: Long): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    // Format alarm time for display: e.g. "07:30" or "7:30 AM"
    fun formatAlarmTime(hour: Int, minute: Int, is24Hour: Boolean = false): Pair<String, String> {
        return if (is24Hour) {
            val timeStr = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            Pair(timeStr, "")
        } else {
            val period = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val timeStr = String.format(Locale.getDefault(), "%d:%02d", displayHour, minute)
            Pair(timeStr, period)
        }
    }

    // World city relative time and offset info
    data class CityTimeInfo(
        val timeString: String,
        val amPm: String,
        val dateString: String,
        val timeDifferenceString: String,
        val isDaytime: Boolean,
        val hour: Int,
        val minute: Int,
        val second: Int
    )

    fun getCityTimeInfo(timeZoneId: String, is24Hour: Boolean = false): CityTimeInfo {
        val zone = try {
            ZoneId.of(timeZoneId)
        } catch (e: Exception) {
            ZoneId.systemDefault()
        }

        val localZone = ZoneId.systemDefault()
        val now = Instant.now()
        val cityZDT = ZonedDateTime.ofInstant(now, zone)
        val localZDT = ZonedDateTime.ofInstant(now, localZone)

        val cityHour = cityZDT.hour
        val cityMinute = cityZDT.minute
        val citySecond = cityZDT.second

        val (timeStr, amPm) = formatAlarmTime(cityHour, cityMinute, is24Hour)
        val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault())
        val dateStr = cityZDT.format(dateFormatter)

        // Time difference relative to local time
        val localOffsetSeconds = localZone.rules.getOffset(now).totalSeconds
        val cityOffsetSeconds = zone.rules.getOffset(now).totalSeconds
        val diffSeconds = cityOffsetSeconds - localOffsetSeconds
        val diffHours = diffSeconds / 3600
        val diffMins = (Math.abs(diffSeconds) % 3600) / 60

        val cityDate = cityZDT.toLocalDate()
        val localDate = localZDT.toLocalDate()

        val dayDiffStr = when {
            cityDate.isEqual(localDate) -> "Today"
            cityDate.isAfter(localDate) -> "Tomorrow"
            else -> "Yesterday"
        }

        val diffString = when {
            diffHours == 0 && diffMins == 0 -> "Same time, $dayDiffStr"
            diffHours > 0 && diffMins == 0 -> "+$diffHours hrs, $dayDiffStr"
            diffHours < 0 && diffMins == 0 -> "$diffHours hrs, $dayDiffStr"
            diffHours >= 0 -> "+$diffHours hrs ${diffMins}m, $dayDiffStr"
            else -> "$diffHours hrs ${diffMins}m, $dayDiffStr"
        }

        // Daytime is roughly 6:00 AM to 6:00 PM (06:00 to 18:00)
        val isDaytime = cityHour in 6..17

        return CityTimeInfo(
            timeString = timeStr,
            amPm = amPm,
            dateString = dateStr,
            timeDifferenceString = diffString,
            isDaytime = isDaytime,
            hour = cityHour,
            minute = cityMinute,
            second = citySecond
        )
    }

    fun calculateTimeUntilAlarm(hour: Int, minute: Int, daysMask: Int = 0): String {
        val now = LocalDateTime.now()
        val currentDayIndex = now.dayOfWeek.value % 7 // Java DayOfWeek: 1=Mon..7=Sun -> 0=Sun..6=Sat
        val currentHour = now.hour
        val currentMinute = now.minute

        var minMinutesUntil = Long.MAX_VALUE

        if (daysMask == 0) { // Once
            var alarmDateTime = LocalDate.now().atTime(hour, minute)
            if (!alarmDateTime.isAfter(now)) {
                alarmDateTime = alarmDateTime.plusDays(1)
            }
            val diffMins = java.time.Duration.between(now, alarmDateTime).toMinutes()
            minMinutesUntil = diffMins
        } else {
            // Find closest enabled day
            for (dayOffset in 0..7) {
                val targetDayIndex = (currentDayIndex + dayOffset) % 7
                if ((daysMask and (1 shl targetDayIndex)) != 0) {
                    val targetDate = LocalDate.now().plusDays(dayOffset.toLong())
                    val targetDateTime = targetDate.atTime(hour, minute)
                    if (targetDateTime.isAfter(now)) {
                        val diffMins = java.time.Duration.between(now, targetDateTime).toMinutes()
                        if (diffMins < minMinutesUntil) {
                            minMinutesUntil = diffMins
                        }
                    }
                }
            }
        }

        if (minMinutesUntil == Long.MAX_VALUE) {
            return "Alarm disabled"
        }

        val hours = minMinutesUntil / 60
        val mins = minMinutesUntil % 60

        return when {
            hours == 0L && mins <= 1L -> "in less than 1 minute"
            hours == 0L -> "in $mins minutes"
            mins == 0L -> "in $hours hours"
            else -> "in $hours hr $mins min"
        }
    }
}
