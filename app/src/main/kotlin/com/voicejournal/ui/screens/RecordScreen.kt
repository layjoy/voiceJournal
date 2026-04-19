package com.voicejournal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.voicejournal.ui.components.*
import com.voicejournal.viewmodel.RecordViewModel

@Composable
fun RecordScreen(
    viewModel: RecordViewModel,
    onNavigateToTimeline: () -> Unit,
    onNavigateToCapsule: () -> Unit,
    onNavigateToAIInsights: () -> Unit = {}
) {
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val recordingTime by viewModel.recordingTime.collectAsStateWithLifecycle()
    val transcription by viewModel.transcription.collectAsStateWithLifecycle()
    val detectedEmotion by viewModel.detectedEmotion.collectAsStateWithLifecycle()
    val amplitude by viewModel.amplitude.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()

    var showSaveDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E27),
                        Color(0xFF1A1F3A),
                        Color(0xFF0A0E27)
                    )
                )
            )
    ) {
        // 粒子背景
        ParticleBackground()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部导航栏
            TopAppBar(
                title = {
                    Text(
                        "声音日记",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToAIInsights) {
                        Icon(
                            Icons.Default.Psychology,
                            "AI洞察",
                            tint = Color(0xFF6C63FF)
                        )
                    }
                    IconButton(onClick = onNavigateToTimeline) {
                        Icon(
                            Icons.Default.List,
                            "时间轴",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onNavigateToCapsule) {
                        Icon(
                            Icons.Default.Lock,
                            "时光胶囊",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.weight(0.3f))

            // 3D声波可视化
            Advanced3DWaveform(
                isRecording = isRecording,
                amplitude = amplitude,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 录音时长
            AnimatedVisibility(
                visible = isRecording,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Text(
                    text = formatTime(recordingTime),
                    style = MaterialTheme.typography.displayMedium,
                    color = Color(0xFFFF6584)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 情绪卡片
            AnimatedVisibility(
                visible = transcription.isNotEmpty() && !isRecording,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                EmotionCard(
                    emotion = detectedEmotion,
                    transcription = transcription,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // 录音按钮
            PulsatingRecordButton(
                isRecording = isRecording,
                onClick = {
                    if (isRecording) {
                        viewModel.stopRecording()
                    } else {
                        viewModel.startRecording()
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 操作按钮
            AnimatedVisibility(
                visible = transcription.isNotEmpty() && !isRecording,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.saveJournalEntry() },
                        enabled = !isSaving,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6C63FF)
                        ),
                        modifier = Modifier.height(56.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.Default.Save, "保存")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("保存日记")
                        }
                    }

                    OutlinedButton(
                        onClick = { showSaveDialog = true },
                        enabled = !isSaving,
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(Icons.Default.Lock, "胶囊")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("时光胶囊")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.3f))
        }

        // 保存为时光胶囊对话框
        if (showSaveDialog) {
            SaveCapsuleDialog(
                onDismiss = { showSaveDialog = false },
                onConfirm = { days ->
                    val unlockTime = System.currentTimeMillis() + days * 24 * 60 * 60 * 1000L
                    viewModel.saveJournalEntry(isCapsule = true, unlockTime = unlockTime)
                    showSaveDialog = false
                }
            )
        }
    }
}

@Composable
fun SaveCapsuleDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedDays by remember { mutableStateOf(7) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("创建时光胶囊") },
        text = {
            Column {
                Text("选择解锁时间")
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(7, 30, 90, 365).forEach { days ->
                        FilterChip(
                            selected = selectedDays == days,
                            onClick = { selectedDays = days },
                            label = {
                                Text(
                                    when (days) {
                                        7 -> "7天"
                                        30 -> "30天"
                                        90 -> "90天"
                                        365 -> "1年"
                                        else -> "${days}天"
                                    }
                                )
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedDays) }) {
                Text("创建")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}
