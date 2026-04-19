package com.voicejournal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.voicejournal.utils.DateUtils
import com.voicejournal.viewmodel.BackupViewModel
import java.io.File

@Composable
fun BackupScreen(
    viewModel: BackupViewModel,
    onNavigateBack: () -> Unit
) {
    val backupFiles by viewModel.backupFiles.collectAsStateWithLifecycle()
    val isCreatingBackup by viewModel.isCreatingBackup.collectAsStateWithLifecycle()
    val isRestoring by viewModel.isRestoring.collectAsStateWithLifecycle()
    var showRestoreDialog by remember { mutableStateOf<File?>(null) }
    var showDeleteDialog by remember { mutableStateOf<File?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadBackupFiles()
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
                            Icons.Default.Backup,
                            contentDescription = null,
                            tint = Color(0xFF6C63FF)
                        )
                        Text("备份与恢复", style = MaterialTheme.typography.headlineMedium)
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
                // 创建备份按钮
                Button(
                    onClick = { viewModel.createBackup() },
                    enabled = !isCreatingBackup && !isRestoring,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6C63FF)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isCreatingBackup) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("创建中...")
                    } else {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("创建新备份", style = MaterialTheme.typography.titleMedium)
                    }
                }

                // 备份文件列表
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1F3A).copy(alpha = 0.8f)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "备份历史",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (backupFiles.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CloudOff,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        "暂无备份",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(backupFiles) { file ->
                                    BackupFileItem(
                                        file = file,
                                        onRestore = { showRestoreDialog = file },
                                        onDelete = { showDeleteDialog = file },
                                        enabled = !isCreatingBackup && !isRestoring
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 恢复确认对话框
        showRestoreDialog?.let { file ->
            AlertDialog(
                onDismissRequest = { showRestoreDialog = null },
                title = { Text("确认恢复") },
                text = { Text("恢复备份将覆盖当前所有数据，是否继续？") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.restoreBackup(file)
                            showRestoreDialog = null
                        }
                    ) {
                        Text("恢复")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRestoreDialog = null }) {
                        Text("取消")
                    }
                }
            )
        }

        // 删除确认对话框
        showDeleteDialog?.let { file ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("确认删除") },
                text = { Text("确定要删除这个备份文件吗？") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteBackup(file)
                            showDeleteDialog = null
                        }
                    ) {
                        Text("删除", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
fun BackupFileItem(
    file: File,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
    enabled: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2F4A).copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = DateUtils.formatDateTime(file.lastModified()),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = formatFileSize(file.length()),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onRestore,
                    enabled = enabled
                ) {
                    Icon(
                        Icons.Default.Restore,
                        contentDescription = "恢复",
                        tint = if (enabled) Color(0xFF6C63FF) else Color.Gray
                    )
                }
                IconButton(
                    onClick = onDelete,
                    enabled = enabled
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = if (enabled) Color.Red else Color.Gray
                    )
                }
            }
        }
    }
}

fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}
