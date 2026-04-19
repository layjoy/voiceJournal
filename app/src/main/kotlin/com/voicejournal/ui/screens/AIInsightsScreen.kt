package com.voicejournal.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import com.voicejournal.viewmodel.AIInsightsViewModel

@Composable
fun AIInsightsScreen(
    viewModel: AIInsightsViewModel,
    onNavigateBack: () -> Unit
) {
    val insights by viewModel.insights.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val emotionTrend by viewModel.emotionTrend.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadInsights()
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
                            Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color(0xFF6C63FF)
                        )
                        Text(
                            "AI洞察",
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
                        CircularProgressIndicator(color = Color(0xFF6C63FF))
                        Text(
                            "AI正在分析你的日记...",
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
                    // 情绪趋势卡片
                    item {
                        EmotionTrendCard(emotionTrend)
                    }

                    // 情绪模式
                    if (insights.patterns.isNotEmpty()) {
                        item {
                            InsightCard(
                                title = "情绪模式",
                                icon = Icons.Default.TrendingUp,
                                items = insights.patterns,
                                color = Color(0xFF6C63FF)
                            )
                        }
                    }

                    // 关注主题
                    if (insights.themes.isNotEmpty()) {
                        item {
                            InsightCard(
                                title = "关注主题",
                                icon = Icons.Default.Topic,
                                items = insights.themes,
                                color = Color(0xFF4ECDC4)
                            )
                        }
                    }

                    // 积极变化
                    if (insights.positiveChanges.isNotEmpty()) {
                        item {
                            InsightCard(
                                title = "积极变化",
                                icon = Icons.Default.TrendingUp,
                                items = insights.positiveChanges,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }

                    // 需要关注
                    if (insights.concerns.isNotEmpty()) {
                        item {
                            InsightCard(
                                title = "需要关注",
                                icon = Icons.Default.Warning,
                                items = insights.concerns,
                                color = Color(0xFFFF9800)
                            )
                        }
                    }

                    // 个性化建议
                    if (insights.suggestions.isNotEmpty()) {
                        item {
                            InsightCard(
                                title = "个性化建议",
                                icon = Icons.Default.Lightbulb,
                                items = insights.suggestions,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmotionTrendCard(trend: String?) {
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
                    Icons.Default.ShowChart,
                    contentDescription = null,
                    tint = Color(0xFF6C63FF),
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    "情绪趋势预测",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = trend ?: "暂无数据",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5f
            )
        }
    }
}

@Composable
fun InsightCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<String>,
    color: Color
) {
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
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(color)
                            .align(Alignment.CenterVertically)
                    )
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
