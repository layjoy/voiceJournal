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
import com.voicejournal.ui.components.ParticleBackground
import com.voicejournal.viewmodel.ExportViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExportScreen(
    viewModel: ExportViewModel,
    onNavigateBack: () -> Unit
) {
    val isExporting by viewModel.isExporting.collectAsStateWithLifecycle()
    val exportProgress by viewModel.exportProgress.collectAsStateWithLifecycle()

    var selectedFormat by remember { mutableStateOf("txt") }
    var selectedDateRange by remember { mutableStateOf("all") }
    var includeAudio by remember { mutableStateOf(false) }
    var includeEmotions by remember { mutableStateOf(true) }
    var includeTags by remember { mutableStateOf(true) }

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
                            Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = Color(0xFF6C63FF)
                        )
                        Text("导出数据", style = MaterialTheme.typography.headlineMedium)
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 导出格式
                ExportSection(title = "导出格式") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FormatChip(
                            label = "TXT",
                            selected = selectedFormat == "txt",
                            onClick = { selectedFormat = "txt" },
                            modifier = Modifier.weight(1f)
                        )
                        FormatChip(
                            label = "JSON",
                            selected = selectedFormat == "json",
                            onClick = { selectedFormat = "json" },
                            modifier = Modifier.weight(1f)
                        )
                        FormatChip(
                            label = "CSV",
                            selected = selectedFormat == "csv",
                            onClick = { selectedFormat = "csv" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 时间范围
                ExportSection(title = "时间范围") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DateRangeOption(
                            label = "全部",
                            selected = selectedDateRange == "all",
                            onClick = { selectedDateRange = "all" }
                        )
                        DateRangeOption(
                            label = "最近7天",
                            selected = selectedDateRange == "week",
                            onClick = { selectedDateRange = "week" }
                        )
                        DateRangeOption(
                            label = "最近30天",
                            selected = selectedDateRange == "month",
                            onClick = { selectedDateRange = "month" }
                        )
                        DateRangeOption(
                            label = "最近90天",
                            selected = selectedDateRange == "quarter",
                            onClick = { selectedDateRange = "quarter" }
                        )
                    }
                }

                // 导出选项
                ExportSection(title = "导出内容") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ExportOption(
                            label = "包含音频文件",
                            description = "导出原始录音文件",
                            checked = includeAudio,
                            onCheckedChange = { includeAudio = it }
                        )
                        ExportOption(
                            label = "包含情绪数据",
                            description = "导出情绪分析结果",
                            checked = includeEmotions,
                            onCheckedChange = { includeEmotions = it }
                        )
                        ExportOption(
                            label = "包含标签",
                            description = "导出日记标签",
                            checked = includeTags,
                            onCheckedChange = { includeTags = it }
                        )
                    }
                }

                // 导出按钮
                Button(
                    onClick = {
                        viewModel.exportData(
                            format = selectedFormat,
                            dateRange = selectedDateRange,
                            includeAudio = includeAudio,
                            includeEmotions = includeEmotions,
                            includeTags = includeTags
                        )
                    },
                    enabled = !isExporting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6C63FF)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("导出中... ${(exportProgress * 100).toInt()}%")
                    } else {
                        Icon(Icons.Default.FileDownload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("开始导出", style = MaterialTheme.typography.titleMedium)
                    }
                }

                // 说明
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1F3A).copy(alpha = 0.6f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF6C63FF)
                        )
                        Column {
                            Text(
                                "导出说明",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "导出的文件将保存到下载目录，可以通过文件管理器访问。",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExportSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF6C63FF),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1F3A).copy(alpha = 0.8f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                content = content
            )
        }
    }
}

@Composable
fun FormatChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF6C63FF) else Color(0xFF2A2F4A),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(label, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
fun DateRangeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Color(0xFF6C63FF).copy(alpha = 0.2f) else Color.Transparent)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF6C63FF)
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
    }
}

@Composable
fun ExportOption(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF6C63FF),
                checkedTrackColor = Color(0xFF6C63FF).copy(alpha = 0.5f)
            )
        )
    }
}
