package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ClockFeedbackManager(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null
    private var ringingJob: Job? = null

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 90)
        } catch (e: Exception) {
            // ToneGenerator might fail in restricted environments
        }
    }

    fun playClickSound() {
        try {
            ToneGenerator(AudioManager.STREAM_NOTIFICATION, 50).apply {
                startTone(ToneGenerator.TONE_PROP_BEEP, 30)
                release()
            }
        } catch (e: Exception) {
            // Ignored
        }
    }

    fun playLapSound() {
        try {
            ToneGenerator(AudioManager.STREAM_NOTIFICATION, 60).apply {
                startTone(ToneGenerator.TONE_PROP_ACK, 40)
                release()
            }
        } catch (e: Exception) {
            // Ignored
        }
        vibrateShort()
    }

    fun vibrateShort() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40)
            }
        } catch (e: Exception) {
            // Ignored
        }
    }

    fun startContinuousAlert(scope: CoroutineScope, isVibrate: Boolean = true) {
        stopContinuousAlert()
        ringingJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 350)
                } catch (e: Exception) {
                    // Ignored
                }

                if (isVibrate) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 200, 300), -1))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator?.vibrate(longArrayOf(0, 300, 200, 300), -1)
                        }
                    } catch (e: Exception) {
                        // Ignored
                    }
                }

                delay(1000)
            }
        }
    }

    fun stopContinuousAlert() {
        ringingJob?.cancel()
        ringingJob = null
        try {
            toneGenerator?.stopTone()
            vibrator?.cancel()
        } catch (e: Exception) {
            // Ignored
        }
    }
}
