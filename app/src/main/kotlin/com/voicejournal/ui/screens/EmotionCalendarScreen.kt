package com.voicejournal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import com.voicejournal.data.model.Emotion
import com.voicejournal.ui.components.ParticleBackground
import com.voicejournal.viewmodel.EmotionCalendarViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EmotionCalendarScreen(
    viewModel: EmotionCalendarViewModel,
    onNavigateBack: () -> Unit,
    onDateClick: (Long) -> Unit
) {
    val calendarData by viewModel.calendarData.collectAsStateWithLifecycle()
    val currentMonth by viewModel.currentMonth.collectAsStateWithLifecycle()
    val emotionStats by viewModel.emotionStats.collectAsStateWithLifecycle()

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
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color(0xFF6C63FF)
                        )
                        Text(
                            "情绪日历",
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 月份选择
                MonthSelector(
                    currentMonth = currentMonth,
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() }
                )

                // 日历网格
                CalendarGrid(
                    calendarData = calendarData,
                    onDateClick = onDateClick
                )

                // 情绪统计
                EmotionStatsCard(emotionStats)
            }
        }
    }
}

@Composable
fun MonthSelector(
    currentMonth: Long,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F3A).copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    Icons.Default.ChevronLeft,
                    "上个月",
                    tint = Color.White
                )
            }

            Text(
                text = formatMonth(currentMonth),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            IconButton(onClick = onNextMonth) {
                Icon(
                    Icons.Default.ChevronRight,
                    "下个月",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun CalendarGrid(
    calendarData: Map<Int, Emotion?>,
    onDateClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F3A).copy(alpha = 0.8f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 星期标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 日期网格
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(300.dp)
            ) {
                items((1..31).toList()) { day ->
                    CalendarDayCell(
                        day = day,
                        emotion = calendarData[day],
                        onClick = { /* onDateClick */ }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    day: Int,
    emotion: Emotion?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                if (emotion != null) {
                    Color(emotion.color).copy(alpha = 0.3f)
                } else {
                    Color(0xFF2A2F4A)
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = if (emotion != null) Color.White else Color.Gray
        )
    }
}

@Composable
fun EmotionStatsCard(stats: Map<Emotion, Int>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F3A).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "本月情绪统计",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            stats.forEach { (emotion, count) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
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
                                .clip(CircleShape)
                                .background(Color(emotion.color))
                        )
                        Text(
                            emotion.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }

                    Text(
                        "$count 次",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

fun formatMonth(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy年MM月", Locale.CHINA)
    return sdf.format(Date(timestamp))
}
