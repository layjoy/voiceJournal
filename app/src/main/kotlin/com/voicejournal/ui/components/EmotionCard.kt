package com.voicejournal.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.voicejournal.data.model.Emotion
import com.voicejournal.emotion.EmotionAnalyzer

@Composable
fun EmotionCard(
    emotion: Emotion,
    transcription: String,
    modifier: Modifier = Modifier
) {
    val emotionAnalyzer = EmotionAnalyzer()

    val backgroundColor by animateColorAsState(
        targetValue = Color(emotion.color).copy(alpha = 0.15f),
        animationSpec = tween(800),
        label = "bgColor"
    )

    val borderColor by animateColorAsState(
        targetValue = Color(emotion.color).copy(alpha = 0.5f),
        animationSpec = tween(800),
        label = "borderColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.05f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "识别结果",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(emotion.color),
                                        Color(emotion.color).copy(alpha = glowAlpha)
                                    )
                                )
                            )
                    )

                    Text(
                        text = emotionAnalyzer.getEmotionDescription(emotion),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(emotion.color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = transcription,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5f
            )
        }
    }
}
