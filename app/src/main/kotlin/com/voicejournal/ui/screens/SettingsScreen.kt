package com.voicejournal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.voicejournal.ui.components.ParticleBackground
import com.voicejournal.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onNavigateToExport: () -> Unit,
    onNavigateToAIConfig: () -> Unit = {}
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val dailyReminderEnabled by viewModel.dailyReminderEnabled.collectAsStateWithLifecycle()
    val autoBackupEnabled by viewModel.autoBackupEnabled.collectAsStateWithLifecycle()
    val audioQuality by viewModel.audioQuality.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val biometricEnabled by viewModel.biometricEnabled.collectAsStateWithLifecycle()

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
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = Color(0xFF6C63FF)
                        )
                        Text("设置", style = MaterialTheme.typography.headlineMedium)
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
                // 外观设置
                SettingsSection(title = "外观") {
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        title = "主题模式",
                        subtitle = when (themeMode) {
                            "light" -> "浅色"
                            "dark" -> "深色"
                            else -> "跟随系统"
                        },
                        onClick = { /* 显示主题选择对话框 */ }
                    )
                    SettingsItem(
                        icon = Icons.Default.Language,
                        title = "语言",
                        subtitle = when (language) {
                            "zh" -> "简体中文"
                            "en" -> "English"
                            else -> "简体中文"
                        },
                        onClick = { /* 显示语言选择对话框 */ }
                    )
                }

                // 通知设置
                SettingsSection(title = "通知") {
                    SettingsSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "启用通知",
                        subtitle = "接收应用通知",
                        checked = notificationsEnabled,
                        onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                    )
                    SettingsSwitchItem(
                        icon = Icons.Default.Schedule,
                        title = "每日提醒",
                        subtitle = "每天提醒记录情绪",
                        checked = dailyReminderEnabled,
                        enabled = notificationsEnabled,
                        onCheckedChange = { viewModel.setDailyReminderEnabled(it) }
                    )
                }

                // 音频设置
                SettingsSection(title = "音频") {
                    SettingsItem(
                        icon = Icons.Default.HighQuality,
                        title = "音频质量",
                        subtitle = when (audioQuality) {
                            "high" -> "高质量"
                            "medium" -> "中等"
                            else -> "标准"
                        },
                        onClick = { /* 显示质量选择对话框 */ }
                    )
                }

                // 数据与隐私
                SettingsSection(title = "数据与隐私") {
                    SettingsSwitchItem(
                        icon = Icons.Default.Fingerprint,
                        title = "生物识别",
                        subtitle = "使用指纹或面容解锁",
                        checked = biometricEnabled,
                        onCheckedChange = { viewModel.setBiometricEnabled(it) }
                    )
                    SettingsSwitchItem(
                        icon = Icons.Default.Backup,
                        title = "自动备份",
                        subtitle = "定期自动备份数据",
                        checked = autoBackupEnabled,
                        onCheckedChange = { viewModel.setAutoBackupEnabled(it) }
                    )
                    SettingsItem(
                        icon = Icons.Default.CloudUpload,
                        title = "备份与恢复",
                        subtitle = "管理数据备份",
                        onClick = onNavigateToBackup
                    )
                    SettingsItem(
                        icon = Icons.Default.FileDownload,
                        title = "导出数据",
                        subtitle = "导出日记为文件",
                        onClick = onNavigateToExport
                    )
                }

                // AI 设置
                SettingsSection(title = "AI 功能") {
                    SettingsItem(
                        icon = Icons.Default.SmartToy,
                        title = "AI 分析配置",
                        subtitle = "配置 AI 提供商和 API Key",
                        onClick = onNavigateToAIConfig
                    )
                }

                // 关于
                SettingsSection(title = "关于") {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "版本",
                        subtitle = "v2.1.0",
                        onClick = { }
                    )
                    SettingsItem(
                        icon = Icons.Default.Description,
                        title = "隐私政策",
                        subtitle = "查看隐私政策",
                        onClick = { }
                    )
                    SettingsItem(
                        icon = Icons.Default.Gavel,
                        title = "用户协议",
                        subtitle = "查看用户协议",
                        onClick = { }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF6C63FF),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
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
                modifier = Modifier.padding(vertical = 8.dp),
                content = content
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF6C63FF),
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) Color(0xFF6C63FF) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) Color.White else Color.Gray
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF6C63FF),
                checkedTrackColor = Color(0xFF6C63FF).copy(alpha = 0.5f)
            )
        )
    }
}
