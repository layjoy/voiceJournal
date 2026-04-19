package com.voicejournal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AudioPlayerCard(
    audioPath: String,
    duration: Int,
    isPlaying: Boolean,
    currentPosition: Int,
    playbackSpeed: Float,
    onPlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onSpeedChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSpeedMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F3A).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 播放控制
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 后退15秒
                IconButton(
                    onClick = { onSeek((currentPosition - 15000).coerceAtLeast(0)) }
                ) {
                    Icon(
                        Icons.Default.Replay,
                        "后退15秒",
                        tint = Color.White
                    )
                }

                // 播放/暂停按钮
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6C63FF)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onPlayPause,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "暂停" else "播放",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // 快进15秒
                IconButton(
                    onClick = { onSeek((currentPosition + 15000).coerceAtMost(duration * 1000)) }
                ) {
                    Icon(
                        Icons.Default.Forward30,
                        "快进15秒",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 进度条
            Column {
                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = { onSeek(it.toInt()) },
                    valueRange = 0f..(duration * 1000).toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF6C63FF),
                        activeTrackColor = Color(0xFF6C63FF),
                        inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        formatTime(currentPosition / 1000),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        formatTime(duration),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 播放速度
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "播放速度",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Box {
                    TextButton(onClick = { showSpeedMenu = true }) {
                        Text(
                            "${playbackSpeed}x",
                            color = Color(0xFF6C63FF)
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFF6C63FF)
                        )
                    }

                    DropdownMenu(
                        expanded = showSpeedMenu,
                        onDismissRequest = { showSpeedMenu = false }
                    ) {
                        listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                            DropdownMenuItem(
                                text = { Text("${speed}x") },
                                onClick = {
                                    onSpeedChange(speed)
                                    showSpeedMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}
