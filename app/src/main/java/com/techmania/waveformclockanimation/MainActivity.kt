package com.techmania.waveformclockanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmania.waveformclockanimation.ui.theme.WaveformClockAnimationTheme
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WaveformClockAnimationTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    WaveformClock()
                }
            }
        }
    }
}

@Composable
fun WaveformClock() {
    val infiniteTransition = rememberInfiniteTransition()
    val angleSeconds by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    var calendar by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(Unit) {
        while (true) {
            calendar = Calendar.getInstance()
            delay(1000L)
        }
    }

    val seconds = calendar.get(Calendar.SECOND)
    val minutes = calendar.get(Calendar.MINUTE)
    val hours = calendar.get(Calendar.HOUR)

    Canvas(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        val radius = size.minDimension / 2
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Draw waveform
        val waveformRadius = radius * 0.8f
        val waveformStrokeWidth = 4.dp.toPx()
        val gradientBrush = Brush.sweepGradient(
            colors = listOf(Color.Red, Color.Magenta, Color.Blue, Color.Cyan, Color.Green, Color.Yellow, Color.Red)
        )
        for (i in 0 until 360 step 5) {
            val angle = Math.toRadians(i.toDouble())
            val amplitude = 10.dp.toPx() * cos(angle * 5 + angleSeconds / 180 * Math.PI)
            val startX = centerX + (waveformRadius + amplitude) * cos(angle).toFloat()
            val startY = centerY + (waveformRadius + amplitude) * sin(angle).toFloat()
            val endX = centerX + waveformRadius * cos(angle).toFloat()
            val endY = centerY + waveformRadius * sin(angle).toFloat()
            drawLine(
                brush = gradientBrush,
                start = androidx.compose.ui.geometry.Offset(startX.toFloat(), startY.toFloat()),
                end = androidx.compose.ui.geometry.Offset(endX, endY),
                strokeWidth = waveformStrokeWidth
            )
        }

        // Draw hour hand
        rotate(degrees = (hours % 12 + minutes / 60f) * 30f) {
            drawLine(
                color = Color.White,
                start = androidx.compose.ui.geometry.Offset(centerX, centerY),
                end = androidx.compose.ui.geometry.Offset(centerX, centerY - radius / 2),
                strokeWidth = 8f,
                cap = StrokeCap.Round
            )
        }

        // Draw minute hand
        rotate(degrees = (minutes + seconds / 60f) * 6f) {
            drawLine(
                color = Color.White,
                start = androidx.compose.ui.geometry.Offset(centerX, centerY),
                end = androidx.compose.ui.geometry.Offset(centerX, centerY - radius * 0.7f),
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
        }

        // Draw second hand
        rotate(degrees = seconds * 6f) {
            drawLine(
                color = Color.Red,
                start = androidx.compose.ui.geometry.Offset(centerX, centerY),
                end = androidx.compose.ui.geometry.Offset(centerX, centerY - radius * 0.9f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WaveformClockAnimationTheme {
        WaveformClock()
    }
}
