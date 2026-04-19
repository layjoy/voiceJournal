package com.voicejournal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Advanced3DWaveform(
    isRecording: Boolean,
    amplitude: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave3d")

    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase1"
    )

    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase2"
    )

    val phase3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase3"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val centerY = height / 2

        val normalizedAmplitude = (amplitude / 32768f).coerceIn(0f, 1f)
        val baseAmplitude = if (isRecording) 60f else 30f
        val dynamicAmplitude = baseAmplitude * (1f + normalizedAmplitude * 3f)

        // 绘制3D波形层
        draw3DWaveLayer(
            centerX = centerX,
            centerY = centerY,
            phase = phase1,
            rotation = rotation,
            amplitude = dynamicAmplitude * 1.2f,
            color = Color(0xFF6C63FF),
            depth = 0f
        )

        draw3DWaveLayer(
            centerX = centerX,
            centerY = centerY,
            phase = phase2,
            rotation = rotation + 120f,
            amplitude = dynamicAmplitude,
            color = Color(0xFF4ECDC4),
            depth = 20f
        )

        draw3DWaveLayer(
            centerX = centerX,
            centerY = centerY,
            phase = phase3,
            rotation = rotation + 240f,
            amplitude = dynamicAmplitude * 0.8f,
            color = Color(0xFFFF6584),
            depth = 40f
        )

        // 绘制中心光晕
        if (isRecording) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6C63FF).copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = dynamicAmplitude * 2
                ),
                center = Offset(centerX, centerY),
                radius = dynamicAmplitude * 2
            )
        }
    }
}

private fun DrawScope.draw3DWaveLayer(
    centerX: Float,
    centerY: Float,
    phase: Float,
    rotation: Float,
    amplitude: Float,
    color: Color,
    depth: Float
) {
    val path = Path()
    val points = 100
    val radius = 150f

    for (i in 0..points) {
        val angle = (i.toFloat() / points) * 360f + rotation
        val rad = Math.toRadians(angle.toDouble())

        // 3D波形计算
        val wave = sin((angle + phase) * Math.PI / 180) * amplitude
        val r = radius + wave.toFloat()

        // 3D投影
        val x = centerX + r * cos(rad).toFloat()
        val y = centerY + r * sin(rad).toFloat() * 0.6f - depth

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }

    path.close()

    drawPath(
        path = path,
        color = color.copy(alpha = 0.7f),
        style = Stroke(width = 3f)
    )
}
