package com.voicejournal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.voicejournal.data.model.Emotion

@Composable
fun EmotionLineChart(
    data: List<Pair<Long, Emotion>>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chart")
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        if (data.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val padding = 40f

        // 绘制坐标轴
        drawLine(
            color = Color.Gray.copy(alpha = 0.3f),
            start = Offset(padding, height - padding),
            end = Offset(width - padding, height - padding),
            strokeWidth = 2f
        )

        drawLine(
            color = Color.Gray.copy(alpha = 0.3f),
            start = Offset(padding, padding),
            end = Offset(padding, height - padding),
            strokeWidth = 2f
        )

        // 绘制数据点和连线
        if (data.size > 1) {
            val path = Path()
            val stepX = (width - 2 * padding) / (data.size - 1)

            data.forEachIndexed { index, (_, emotion) ->
                val x = padding + index * stepX
                val y = height - padding - (emotionToValue(emotion) * (height - 2 * padding))

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }

                // 绘制数据点
                drawCircle(
                    color = Color(emotion.color),
                    radius = 6f,
                    center = Offset(x, y)
                )
            }

            // 绘制连线
            drawPath(
                path = path,
                color = Color(0xFF6C63FF),
                style = Stroke(width = 3f)
            )
        }
    }
}

@Composable
fun EmotionBarChart(
    data: Map<Emotion, Int>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        if (data.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val padding = 40f

        val maxValue = data.values.maxOrNull() ?: 1
        val barWidth = (width - 2 * padding) / data.size
        val barSpacing = barWidth * 0.2f

        data.entries.forEachIndexed { index, (emotion, count) ->
            val x = padding + index * barWidth
            val barHeight = (count.toFloat() / maxValue) * (height - 2 * padding)
            val y = height - padding - barHeight

            drawRect(
                color = Color(emotion.color),
                topLeft = Offset(x + barSpacing / 2, y),
                size = androidx.compose.ui.geometry.Size(
                    barWidth - barSpacing,
                    barHeight
                )
            )
        }
    }
}

private fun emotionToValue(emotion: Emotion): Float {
    return when (emotion) {
        Emotion.HAPPY -> 0.9f
        Emotion.EXCITED -> 0.8f
        Emotion.CALM -> 0.6f
        Emotion.NEUTRAL -> 0.5f
        Emotion.ANXIOUS -> 0.3f
        Emotion.SAD -> 0.2f
    }
}
