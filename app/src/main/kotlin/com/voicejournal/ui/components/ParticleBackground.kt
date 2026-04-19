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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ParticleBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 生成粒子
        val particles = List(50) { i ->
            Particle(
                x = Random.nextFloat() * width,
                y = Random.nextFloat() * height,
                radius = Random.nextFloat() * 3f + 1f,
                speed = Random.nextFloat() * 0.5f + 0.2f,
                angle = Random.nextFloat() * 360f
            )
        }

        particles.forEach { particle ->
            val offsetX = cos(Math.toRadians((time + particle.angle).toDouble())).toFloat() * particle.speed * 50
            val offsetY = sin(Math.toRadians((time + particle.angle).toDouble())).toFloat() * particle.speed * 50

            val x = (particle.x + offsetX) % width
            val y = (particle.y + offsetY) % height

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6C63FF).copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    center = Offset(x, y),
                    radius = particle.radius * 3
                ),
                center = Offset(x, y),
                radius = particle.radius * 3
            )

            drawCircle(
                color = Color(0xFF6C63FF).copy(alpha = 0.6f),
                center = Offset(x, y),
                radius = particle.radius
            )
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val speed: Float,
    val angle: Float
)
