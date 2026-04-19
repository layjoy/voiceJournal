package com.voicejournal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun AudioWaveformBar(
    waveformData: List<Float>,
    isPlaying: Boolean,
    currentPosition: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    val animatedPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "position"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2

        val barWidth = 4.dp.toPx()
        val barSpacing = 2.dp.toPx()
        val totalBarWidth = barWidth + barSpacing
        val barCount = (width / totalBarWidth).toInt()

        // 生成或使用波形数据
        val data = if (waveformData.isNotEmpty()) {
            waveformData
        } else {
            List(barCount) { i ->
                (sin(i * 0.5) * 0.5f + 0.5f).toFloat()
            }
        }

        for (i in 0 until barCount) {
            val x = i * totalBarWidth
            val dataIndex = (i * data.size / barCount).coerceIn(0, data.size - 1)
            val amplitude = data[dataIndex]
            val barHeight = amplitude * height * 0.8f

            val progress = if (isPlaying) animatedPosition else currentPosition
            val isPassed = (i.toFloat() / barCount) < progress

            val color = if (isPassed) {
                Color(0xFF6C63FF)
            } else {
                Color(0xFF4A4A6A)
            }

            drawRoundRect(
                color = color,
                topLeft = Offset(x, centerY - barHeight / 2),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2)
            )
        }
    }
}
