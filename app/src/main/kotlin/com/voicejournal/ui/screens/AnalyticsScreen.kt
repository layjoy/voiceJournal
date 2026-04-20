package com.voicejournal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.voicejournal.analytics.EmotionAnalytics
import com.voicejournal.ui.components.EmotionLineChart
import com.voicejournal.ui.components.EmotionBarChart
import com.voicejournal.ui.components.ParticleBackground
import com.voicejournal.viewmodel.AnalyticsViewModel

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onNavigateBack: () -> Unit
) {
    val emotionTrends by viewModel.emotionTrends.collectAsStateWithLifecycle()
    val timePatterns by viewModel.timePatterns.collectAsStateWithLifecycle()
    val weeklyReport by viewModel.weeklyReport.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadAnalytics()
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
                            Icons.Default.Analytics,
                            contentDescription = null,
                            tint = Color(0xFF6C63FF)
                        )
                        Text("数据分析", style = MaterialTheme.typography.headlineMedium)
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
                    CircularProgressIndicator(color = Color(0xFF6C63FF))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 情绪趋势
                    AnalyticsCard(title = "情绪趋势 (30天)") {
                        if (emotionTrends.isEmpty()) {
                            EmptyState("暂无数据")
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                emotionTrends.forEach { trend ->
                                    EmotionTrendItem(trend)
                                }
                            }
                        }
                    }

                    // 时间模式
                    AnalyticsCard(title = "时间分布") {
                        if (timePatterns.isEmpty()) {
                            EmptyState("暂无数据")
                        } else {
                            Text("时间分布图表", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    // 周报告
                    weeklyReport?.let { report ->
                        AnalyticsCard(title = "本周报告") {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                ReportItem(
                                    label = "总记录数",
                                    value = "${report.totalEntries} 条"
                                )
                                ReportItem(
                                    label = "主要情绪",
                                    value = report.dominantEmotion.displayName
                                )
                                ReportItem(
                                    label = "情绪稳定性",
                                    value = "稳定"
                                )

                                if (report.insights.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "洞察",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color(0xFF6C63FF)
                                    )
                                    report.insights.forEach { insight ->
                                        Text(
                                            "• $insight",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 统计卡片
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "平均每天",
                            value = "2.3",
                            unit = "条",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "最长连续",
                            value = "7",
                            unit = "天",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F3A).copy(alpha = 0.8f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun EmotionTrendItem(trend: EmotionAnalytics.EmotionTrend) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(trend.emotion.color))
            )
            Text(
                trend.emotion.name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${trend.count} 次",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Text(
                "${(trend.percentage * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6C63FF)
            )
            Icon(
                imageVector = when (trend.trend) {
                    "up" -> Icons.Default.TrendingUp
                    "down" -> Icons.Default.TrendingDown
                    else -> Icons.Default.TrendingFlat
                },
                contentDescription = null,
                tint = when (trend.trend) {
                    "up" -> Color.Green
                    "down" -> Color.Red
                    else -> Color.Gray
                },
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ReportItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F3A).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF6C63FF)
                )
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}
