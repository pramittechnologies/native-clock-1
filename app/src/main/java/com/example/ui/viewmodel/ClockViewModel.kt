package com.example.ui.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.WorldCitiesProvider
import com.example.data.db.ClockDatabase
import com.example.data.model.Alarm
import com.example.data.model.WorldCity
import com.example.data.repository.ClockRepository
import com.example.util.ClockFeedbackManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

data class LapRecord(
    val lapNumber: Int,
    val lapTimeMillis: Long,
    val overallTimeMillis: Long
)

enum class AlertType {
    ALARM,
    TIMER
}

data class ActiveAlert(
    val type: AlertType,
    val title: String,
    val subtitle: String,
    val alarmId: Long? = null,
    val originalTimerSeconds: Long? = null
)

data class CurrentLocalTime(
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0,
    val millisecond: Int = 0,
    val formattedTime: String = "",
    val amPm: String = "",
    val formattedDate: String = "",
    val timeZoneName: String = "",
    val isDaytime: Boolean = true
)

class ClockViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClockRepository
    val feedbackManager = ClockFeedbackManager(application)

    init {
        val database = ClockDatabase.getDatabase(application, viewModelScope)
        repository = ClockRepository(database.alarmDao(), database.worldCityDao())
    }

    // --- Time Format Preference (12h vs 24h) ---
    private val _is24HourFormat = MutableStateFlow(false)
    val is24HourFormat: StateFlow<Boolean> = _is24HourFormat.asStateFlow()

    fun toggle24HourFormat() {
        _is24HourFormat.value = !_is24HourFormat.value
        updateLocalTime()
    }

    // --- Live Local Time Ticker ---
    private val _localTime = MutableStateFlow(CurrentLocalTime())
    val localTime: StateFlow<CurrentLocalTime> = _localTime.asStateFlow()

    // --- Active Alert State (Alarm ringing or Timer done) ---
    private val _activeAlert = MutableStateFlow<ActiveAlert?>(null)
    val activeAlert: StateFlow<ActiveAlert?> = _activeAlert.asStateFlow()

    // --- ALARMS ---
    val alarms: StateFlow<List<Alarm>> = repository.allAlarms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var lastCheckedMinute: Int = -1

    fun toggleAlarmEnabled(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAlarm(alarm.copy(isEnabled = !alarm.isEnabled))
        }
        feedbackManager.playClickSound()
    }

    fun saveAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            if (alarm.id == 0L) {
                repository.insertAlarm(alarm)
            } else {
                repository.updateAlarm(alarm)
            }
        }
        feedbackManager.playClickSound()
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlarm(alarm)
        }
        feedbackManager.playClickSound()
    }

    fun testAlarm(alarm: Alarm) {
        triggerAlert(
            ActiveAlert(
                type = AlertType.ALARM,
                title = alarm.label.ifBlank { "Alarm" },
                subtitle = String.format("%02d:%02d", alarm.hour, alarm.minute),
                alarmId = alarm.id
            ),
            isVibrate = alarm.isVibrate
        )
    }

    // --- WORLD CLOCK ---
    val savedWorldCities: StateFlow<List<WorldCity>> = repository.allWorldCities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addWorldCity(city: WorldCity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCity(city)
        }
        feedbackManager.playClickSound()
    }

    fun removeWorldCity(city: WorldCity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCity(city)
        }
        feedbackManager.playClickSound()
    }

    // --- STOPWATCH ---
    private val _stopwatchRunning = MutableStateFlow(false)
    val stopwatchRunning: StateFlow<Boolean> = _stopwatchRunning.asStateFlow()

    private val _stopwatchElapsedMillis = MutableStateFlow(0L)
    val stopwatchElapsedMillis: StateFlow<Long> = _stopwatchElapsedMillis.asStateFlow()

    private val _laps = MutableStateFlow<List<LapRecord>>(emptyList())
    val laps: StateFlow<List<LapRecord>> = _laps.asStateFlow()

    private var stopwatchBaseTime: Long = 0L
    private var stopwatchAccumulatedTime: Long = 0L
    private var stopwatchJob: Job? = null
    private var lastLapTotalMillis: Long = 0L

    fun startStopwatch() {
        if (_stopwatchRunning.value) return
        _stopwatchRunning.value = true
        stopwatchBaseTime = SystemClock.elapsedRealtime()
        feedbackManager.playClickSound()

        stopwatchJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive && _stopwatchRunning.value) {
                val now = SystemClock.elapsedRealtime()
                _stopwatchElapsedMillis.value = stopwatchAccumulatedTime + (now - stopwatchBaseTime)
                delay(16) // ~60fps smooth millisecond update
            }
        }
    }

    fun pauseStopwatch() {
        if (!_stopwatchRunning.value) return
        _stopwatchRunning.value = false
        stopwatchJob?.cancel()
        val now = SystemClock.elapsedRealtime()
        stopwatchAccumulatedTime += (now - stopwatchBaseTime)
        _stopwatchElapsedMillis.value = stopwatchAccumulatedTime
        feedbackManager.playClickSound()
    }

    fun resetStopwatch() {
        _stopwatchRunning.value = false
        stopwatchJob?.cancel()
        stopwatchBaseTime = 0L
        stopwatchAccumulatedTime = 0L
        _stopwatchElapsedMillis.value = 0L
        lastLapTotalMillis = 0L
        _laps.value = emptyList()
        feedbackManager.playClickSound()
    }

    fun lapStopwatch() {
        if (!_stopwatchRunning.value && _stopwatchElapsedMillis.value == 0L) return
        val currentTotal = _stopwatchElapsedMillis.value
        val lapSplit = currentTotal - lastLapTotalMillis
        lastLapTotalMillis = currentTotal

        val newLapNumber = _laps.value.size + 1
        val newRecord = LapRecord(
            lapNumber = newLapNumber,
            lapTimeMillis = lapSplit,
            overallTimeMillis = currentTotal
        )
        // Add to front of list for newest lap first
        _laps.value = listOf(newRecord) + _laps.value
        feedbackManager.playLapSound()
    }

    // --- TIMER ---
    private val _timerTotalSeconds = MutableStateFlow(300L) // default 5m
    val timerTotalSeconds: StateFlow<Long> = _timerTotalSeconds.asStateFlow()

    private val _timerRemainingSeconds = MutableStateFlow(300L)
    val timerRemainingSeconds: StateFlow<Long> = _timerRemainingSeconds.asStateFlow()

    private val _timerRunning = MutableStateFlow(false)
    val timerRunning: StateFlow<Boolean> = _timerRunning.asStateFlow()

    private val _timerLabel = MutableStateFlow("Timer")
    val timerLabel: StateFlow<String> = _timerLabel.asStateFlow()

    private var timerJob: Job? = null
    private var timerEndTime: Long = 0L
    private var timerRemainingAtPause: Long = 0L

    fun setTimerDuration(hours: Int, minutes: Int, seconds: Int, label: String = "Timer") {
        val totalSecs = (hours * 3600L) + (minutes * 60L) + seconds.toLong()
        if (totalSecs <= 0) return
        resetTimer()
        _timerTotalSeconds.value = totalSecs
        _timerRemainingSeconds.value = totalSecs
        _timerLabel.value = label
    }

    fun setPresetTimer(totalSecs: Long, label: String) {
        setTimerDuration(
            hours = (totalSecs / 3600).toInt(),
            minutes = ((totalSecs % 3600) / 60).toInt(),
            seconds = (totalSecs % 60).toInt(),
            label = label
        )
        startTimer()
    }

    fun startTimer() {
        if (_timerRunning.value || _timerRemainingSeconds.value <= 0) return
        _timerRunning.value = true
        feedbackManager.playClickSound()

        val remainingMillis = _timerRemainingSeconds.value * 1000L
        timerEndTime = SystemClock.elapsedRealtime() + remainingMillis

        timerJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive && _timerRunning.value) {
                val now = SystemClock.elapsedRealtime()
                val leftMillis = timerEndTime - now
                if (leftMillis <= 0) {
                    _timerRemainingSeconds.value = 0
                    _timerRunning.value = false
                    onTimerFinished()
                    break
                } else {
                    val remainingSecs = Math.ceil(leftMillis / 1000.0).toLong()
                    _timerRemainingSeconds.value = remainingSecs
                }
                delay(100)
            }
        }
    }

    fun pauseTimer() {
        if (!_timerRunning.value) return
        _timerRunning.value = false
        timerJob?.cancel()
        val now = SystemClock.elapsedRealtime()
        val leftMillis = (timerEndTime - now).coerceAtLeast(0)
        _timerRemainingSeconds.value = Math.ceil(leftMillis / 1000.0).toLong()
        feedbackManager.playClickSound()
    }

    fun resetTimer() {
        _timerRunning.value = false
        timerJob?.cancel()
        _timerRemainingSeconds.value = _timerTotalSeconds.value
        feedbackManager.playClickSound()
    }

    fun addOneMinuteToTimer() {
        val newTotal = _timerTotalSeconds.value + 60
        _timerTotalSeconds.value = newTotal
        val newRemaining = _timerRemainingSeconds.value + 60
        _timerRemainingSeconds.value = newRemaining
        if (_timerRunning.value) {
            timerEndTime += 60000L
        }
        feedbackManager.playClickSound()
    }

    private fun onTimerFinished() {
        triggerAlert(
            ActiveAlert(
                type = AlertType.TIMER,
                title = _timerLabel.value.ifBlank { "Timer" },
                subtitle = "Time's up!",
                originalTimerSeconds = _timerTotalSeconds.value
            ),
            isVibrate = true
        )
    }

    // --- ALERTS (Alarm / Timer Ringing) ---
    private fun triggerAlert(alert: ActiveAlert, isVibrate: Boolean = true) {
        _activeAlert.value = alert
        feedbackManager.startContinuousAlert(viewModelScope, isVibrate = isVibrate)
    }

    fun dismissActiveAlert() {
        _activeAlert.value = null
        feedbackManager.stopContinuousAlert()
    }

    fun snoozeActiveAlert(snoozeMinutes: Int = 5) {
        val currentAlert = _activeAlert.value
        dismissActiveAlert()

        // If it was an alarm, schedule a snooze trigger
        viewModelScope.launch(Dispatchers.Default) {
            delay(snoozeMinutes * 60 * 1000L)
            if (currentAlert != null) {
                triggerAlert(currentAlert.copy(subtitle = "Snoozed ($snoozeMinutes min)"))
            }
        }
    }

    // --- Background Periodic Loops ---
    init {
        startClockAndAlarmCheckLoop()
    }

    private fun startClockAndAlarmCheckLoop() {
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                updateLocalTime()
                checkAlarms()
                delay(500)
            }
        }
    }

    private fun updateLocalTime() {
        val now = LocalDateTime.now()
        val zone = ZoneId.systemDefault()
        val is24 = _is24HourFormat.value

        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val second = cal.get(Calendar.SECOND)
        val millisecond = cal.get(Calendar.MILLISECOND)

        val isDaytime = hour in 6..17

        val (formattedTime, amPm) = if (is24) {
            Pair(String.format("%02d:%02d:%02d", hour, minute, second), "")
        } else {
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val period = if (hour >= 12) "PM" else "AM"
            Pair(String.format("%d:%02d:%02d", displayHour, minute, second), period)
        }

        val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
        val formattedDate = now.format(dateFormatter)

        _localTime.value = CurrentLocalTime(
            hour = hour,
            minute = minute,
            second = second,
            millisecond = millisecond,
            formattedTime = formattedTime,
            amPm = amPm,
            formattedDate = formattedDate,
            timeZoneName = zone.id.replace("_", " "),
            isDaytime = isDaytime
        )
    }

    private fun checkAlarms() {
        val now = LocalDateTime.now()
        val currentMinute = now.minute
        val currentHour = now.hour
        val currentSecond = now.second

        // Check only on the start of a minute (second 0..2) and avoid double-triggering in the same minute
        if (currentSecond <= 2 && currentMinute != lastCheckedMinute) {
            lastCheckedMinute = currentMinute
            val currentDayIndex = now.dayOfWeek.value % 7 // 0=Sun .. 6=Sat

            val currentAlarms = alarms.value
            for (alarm in currentAlarms) {
                if (alarm.isEnabled && alarm.hour == currentHour && alarm.minute == currentMinute) {
                    val shouldRing = if (alarm.daysMask == 0) {
                        true // Once
                    } else {
                        alarm.isDaySelected(currentDayIndex)
                    }

                    if (shouldRing) {
                        triggerAlert(
                            ActiveAlert(
                                type = AlertType.ALARM,
                                title = alarm.label.ifBlank { "Alarm" },
                                subtitle = String.format("%02d:%02d", alarm.hour, alarm.minute),
                                alarmId = alarm.id
                            ),
                            isVibrate = alarm.isVibrate
                        )

                        // If it's a one-time alarm, disable it after ringing
                        if (alarm.daysMask == 0) {
                            viewModelScope.launch(Dispatchers.IO) {
                                repository.updateAlarm(alarm.copy(isEnabled = false))
                            }
                        }
                    }
                }
            }
        }
    }
}
