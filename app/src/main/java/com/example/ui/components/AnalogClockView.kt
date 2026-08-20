package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GeoActiveCard
import com.example.ui.theme.GeoError
import com.example.ui.theme.GeoInactiveCard
import com.example.ui.theme.GeoOnBackground
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalogClockView(
    hour: Int,
    minute: Int,
    second: Int,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    showSeconds: Boolean = true,
    isDaytime: Boolean = true,
    accentColor: Color = GeoPrimary
) {
    val dialColor = if (isDaytime) GeoActiveCard.copy(alpha = 0.6f) else GeoInactiveCard
    val rimColor = GeoOutline.copy(alpha = 0.5f)
    val markerColor = GeoOnBackground.copy(alpha = 0.4f)
    val hourHandColor = GeoPrimary
    val minuteHandColor = GeoSecondary
    val secondHandColor = GeoError

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasRadius = this.size.minDimension / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            // Draw dial background
            drawCircle(
                color = dialColor,
                radius = canvasRadius,
                center = center
            )

            // Draw outer rim stroke
            drawCircle(
                color = rimColor,
                radius = canvasRadius - 2.dp.toPx(),
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Draw 12 hour ticks and 60 minute ticks
            for (i in 0 until 60) {
                val angleInRad = (i * 6f - 90f) * (Math.PI / 180f).toFloat()
                val isHourTick = i % 5 == 0
                val tickLength = if (isHourTick) canvasRadius * 0.12f else canvasRadius * 0.05f
                val strokeWidth = if (isHourTick) 2.5.dp.toPx() else 1.dp.toPx()
                val tickAlpha = if (isHourTick) 0.85f else 0.3f

                val startRadius = canvasRadius - 6.dp.toPx() - tickLength
                val endRadius = canvasRadius - 6.dp.toPx()

                val start = Offset(
                    x = center.x + startRadius * cos(angleInRad),
                    y = center.y + startRadius * sin(angleInRad)
                )
                val end = Offset(
                    x = center.x + endRadius * cos(angleInRad),
                    y = center.y + endRadius * sin(angleInRad)
                )

                drawLine(
                    color = markerColor.copy(alpha = tickAlpha),
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            // Calculations for hands
            // Hour hand
            val hourAngle = ((hour % 12 + minute / 60f) * 30f - 90f) * (Math.PI / 180f).toFloat()
            val hourHandLength = canvasRadius * 0.52f
            val hourHandEnd = Offset(
                x = center.x + hourHandLength * cos(hourAngle),
                y = center.y + hourHandLength * sin(hourAngle)
            )
            val hourHandTail = Offset(
                x = center.x - (canvasRadius * 0.1f) * cos(hourAngle),
                y = center.y - (canvasRadius * 0.1f) * sin(hourAngle)
            )
            drawLine(
                color = hourHandColor,
                start = hourHandTail,
                end = hourHandEnd,
                strokeWidth = 4.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Minute hand
            val minuteAngle = ((minute + second / 60f) * 6f - 90f) * (Math.PI / 180f).toFloat()
            val minuteHandLength = canvasRadius * 0.72f
            val minuteHandEnd = Offset(
                x = center.x + minuteHandLength * cos(minuteAngle),
                y = center.y + minuteHandLength * sin(minuteAngle)
            )
            val minuteHandTail = Offset(
                x = center.x - (canvasRadius * 0.12f) * cos(minuteAngle),
                y = center.y - (canvasRadius * 0.12f) * sin(minuteAngle)
            )
            drawLine(
                color = minuteHandColor,
                start = minuteHandTail,
                end = minuteHandEnd,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Second hand
            if (showSeconds) {
                val secondAngle = (second * 6f - 90f) * (Math.PI / 180f).toFloat()
                val secondHandLength = canvasRadius * 0.82f
                val secondHandEnd = Offset(
                    x = center.x + secondHandLength * cos(secondAngle),
                    y = center.y + secondHandLength * sin(secondAngle)
                )
                val secondHandTail = Offset(
                    x = center.x - (canvasRadius * 0.18f) * cos(secondAngle),
                    y = center.y - (canvasRadius * 0.18f) * sin(secondAngle)
                )
                drawLine(
                    color = secondHandColor,
                    start = secondHandTail,
                    end = secondHandEnd,
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Second hand center accent cap
                drawCircle(
                    color = secondHandColor,
                    radius = 3.5.dp.toPx(),
                    center = center
                )
            }

            // Central pivot cap
            drawCircle(
                color = hourHandColor,
                radius = 5.dp.toPx(),
                center = center
            )
        }
    }
}
