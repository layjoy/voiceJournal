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
import com.voicejournal.viewmodel.SocialViewModel
import com.voicejournal.social.SocialPlatform

@Composable
fun SocialScreen(
    viewModel: SocialViewModel,
    onNavigateBack: () -> Unit
) {
    val isSharing by viewModel.isSharing.collectAsStateWithLifecycle()
    var selectedPlatform by remember { mutableStateOf<SocialPlatform?>(null) }

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
                            Icons.Default.Share,
                            contentDescription = null,
                            tint = Color(0xFF6C63FF)
                        )
                        Text("分享", style = MaterialTheme.typography.headlineMedium)
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
                // 分享平台
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
                            "选择分享平台",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            SharePlatformItem(
                                icon = Icons.Default.Message,
                                name = "微信",
                                description = "分享到微信好友",
                                onClick = { selectedPlatform = SocialPlatform.WECHAT }
                            )
                            SharePlatformItem(
                                icon = Icons.Default.Group,
                                name = "朋友圈",
                                description = "分享到微信朋友圈",
                                onClick = { selectedPlatform = SocialPlatform.WECHAT }
                            )
                            SharePlatformItem(
                                icon = Icons.Default.Email,
                                name = "邮件",
                                description = "通过邮件分享",
                                onClick = { selectedPlatform = SocialPlatform.WEIBO }
                            )
                            SharePlatformItem(
                                icon = Icons.Default.Link,
                                name = "复制链接",
                                description = "复制分享链接",
                                onClick = { selectedPlatform = SocialPlatform.QQ }
                            )
                        }
                    }
                }

                // 分享内容预览
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
                            "分享内容",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "我在使用声音日记记录情绪，已经坚持了 30 天！",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "快来一起记录你的情绪吧～",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                // 隐私提示
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
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF6C63FF)
                        )
                        Column {
                            Text(
                                "隐私保护",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "分享时不会包含您的个人日记内容，仅分享统计数据和成就。",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // 分享确认对话框
        selectedPlatform?.let { platform ->
            AlertDialog(
                onDismissRequest = { selectedPlatform = null },
                title = { Text("确认分享") },
                text = { Text("确定要分享到${getPlatformName(platform)}吗？") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.shareToSocial(platform)
                            selectedPlatform = null
                        }
                    ) {
                        Text("分享")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedPlatform = null }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
fun SharePlatformItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2F4A).copy(alpha = 0.6f)
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF6C63FF),
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

fun getPlatformName(platform: SocialPlatform): String {
    return when (platform) {
        SocialPlatform.WECHAT -> "微信"
        SocialPlatform.WEIBO -> "微博"
        SocialPlatform.QQ -> "QQ"
        SocialPlatform.DOUBAN -> "豆瓣"
    }
}
