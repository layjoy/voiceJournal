package com.voicejournal.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.voicejournal.ui.components.ParticleBackground
import com.voicejournal.viewmodel.AIAnalysisViewModel

@Composable
fun AIAnalysisScreen(
    entryId: String,
    viewModel: AIAnalysisViewModel,
    onNavigateBack: () -> Unit
) {
    val deepAnalysis by viewModel.deepAnalysis.collectAsStateWithLifecycle()
    val writingSuggestions by viewModel.writingSuggestions.collectAsStateWithLifecycle()
    val similarEntries by viewModel.similarEntries.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(entryId) {
        viewModel.analyzeEntry(entryId)
    }

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
        ParticleBackground()

        Column {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFFFD700)
                        )
                        Text(
                            "AI深度分析",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFFD700))
                        Text(
                            "AI正在深度分析...",
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 深度情绪分析
                    deepAnalysis?.let { analysis ->
                        item {
                            DeepEmotionAnalysisCard(analysis)
                        }
                    }

                    // 写作建议
                    writingSuggestions?.let { suggestions ->
                        item {
                            WritingSuggestionsCard(suggestions)
                        }
                    }

                    // 相似日记
                    if (similarEntries.isNotEmpty()) {
                        item {
                            SimilarEntriesCard(similarEntries)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeepEmotionAnalysisCard(analysis: com.voicejournal.ai.EmotionAnalysisResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F3A).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Psychology,
                    contentDescription = null,
                    tint = Color(0xFF6C63FF),
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    "深度情绪分析",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 主要情绪
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "主要情绪",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF6C63FF).copy(alpha = 0.2f)
                ) {
                    Text(
                        analysis.primaryEmotion,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF6C63FF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 情绪强度
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "情绪强度",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(10) { index ->
                        Box(
                            modifier = Modifier
                                .size(width = 20.dp, height = 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (index < analysis.intensity) {
                                        Color(0xFF6C63FF)
                                    } else {
                                        Color.Gray.copy(alpha = 0.3f)
                                    }
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 次要情绪
            if (analysis.secondaryEmotions.isNotEmpty()) {
                Text(
                    "次要情绪",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    analysis.secondaryEmotions.forEach { emotion ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF4ECDC4).copy(alpha = 0.2f)
                        ) {
                            Text(
                                emotion,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4ECDC4)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 详细分析
            Divider(color = Color.Gray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                analysis.analysis,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5f
            )
        }
    }
}

@Composable
fun WritingSuggestionsCard(suggestions: com.voicejournal.ai.WritingSuggestionResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F3A).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    "写作建议",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 评分
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScoreItem("表达清晰度", suggestions.clarityScore)
                ScoreItem("情感表达", suggestions.emotionScore)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))

            // 建议列表
            suggestions.suggestions.forEach { suggestion ->
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = suggestion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreItem(label: String, score: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "$score/10",
            style = MaterialTheme.typography.headlineMedium,
            color = when {
                score >= 8 -> Color(0xFF4CAF50)
                score >= 6 -> Color(0xFFFFD700)
                else -> Color(0xFFFF9800)
            }
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun SimilarEntriesCard(entries: List<com.voicejournal.ai.SimilarEntry>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F3A).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.FindInPage,
                    contentDescription = null,
                    tint = Color(0xFF4ECDC4),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    "相似日记",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            entries.forEach { entry ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2A2F4A)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                entry.date,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                "${(entry.similarityScore * 100).toInt()}% 相似",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4ECDC4)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            entry.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "相似原因: ${entry.reason}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
