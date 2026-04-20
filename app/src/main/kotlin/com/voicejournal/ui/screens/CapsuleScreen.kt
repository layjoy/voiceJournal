package com.voicejournal.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.voicejournal.data.model.JournalEntry
import com.voicejournal.ui.components.ParticleBackground
import com.voicejournal.viewmodel.CapsuleViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun CapsuleScreen(
    viewModel: CapsuleViewModel,
    onNavigateBack: () -> Unit
) {
    val capsules by viewModel.capsules.collectAsStateWithLifecycle()
    var selectedCapsule by remember { mutableStateOf<JournalEntry?>(null) }

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
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFFFD700)
                        )
                        Text(
                            "时光胶囊",
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

            if (capsules.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color(0xFFFFD700).copy(alpha = 0.5f)
                        )
                        Text(
                            text = "还没有时光胶囊",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "录制日记时选择\"时光胶囊\"即可创建",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = capsules,
                        key = { capsule: JournalEntry -> capsule.id }
                    ) { capsule: JournalEntry ->
                        EnhancedCapsuleCard(
                            capsule = capsule,
                            isUnlocked = viewModel.isUnlocked(capsule),
                            isExpanded = selectedCapsule?.id == capsule.id,
                            onClick = {
                                if (viewModel.isUnlocked(capsule)) {
                                    selectedCapsule = if (selectedCapsule?.id == capsule.id) null else capsule
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedCapsuleCard(
    capsule: JournalEntry,
    isUnlocked: Boolean,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val scale by animateFloatAsState(
        targetValue = if (isExpanded) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val remainingTime = if (!isUnlocked && capsule.unlockTime != null) {
        capsule.unlockTime - System.currentTimeMillis()
    } else {
        0L
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .clickable(enabled = isUnlocked, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                Color(0xFF1A1F3A).copy(alpha = 0.8f)
            } else {
                Color(0xFF2A2F4A).copy(alpha = 0.6f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpanded) 8.dp else 4.dp
        )
    ) {
        Box {
            // 锁定状态的光晕效果
            if (!isUnlocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700).copy(alpha = glowAlpha * 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = formatDate(capsule.timestamp),
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (isUnlocked) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF4ECDC4).copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.LockOpen,
                                        contentDescription = null,
                                        tint = Color(0xFF4ECDC4),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "已解锁",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF4ECDC4)
                                    )
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                capsule.unlockTime?.let { unlockTime ->
                                    Text(
                                        text = "解锁时间: ${formatDate(unlockTime)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFFFD700)
                                    )

                                    if (remainingTime > 0) {
                                        Text(
                                            text = "还需 ${formatRemainingTime(remainingTime)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 锁图标
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                if (isUnlocked) {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF4ECDC4),
                                            Color(0xFF3DBDAD)
                                        )
                                    )
                                } else {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFFFD700).copy(alpha = glowAlpha),
                                            Color(0xFFFFB700).copy(alpha = glowAlpha)
                                        )
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // 解锁后显示内容
                AnimatedVisibility(
                    visible = isUnlocked,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(20.dp))
                        Divider(color = Color.Gray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = capsule.transcription,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5f
                        )

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(capsule.emotion.color).copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(capsule.emotion.color))
                                        )
                                        Text(
                                            text = capsule.emotion.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(capsule.emotion.color)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF2A2F4A)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Timer,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "${capsule.duration}秒",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }

                        if (!isExpanded && capsule.transcription.length > 100) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = onClick) {
                                Text("展开查看", color = Color(0xFF6C63FF))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
    return sdf.format(Date(timestamp))
}

fun formatRemainingTime(millis: Long): String {
    val days = millis / (24 * 60 * 60 * 1000)
    val hours = (millis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)

    return when {
        days > 0 -> "${days}天${hours}小时"
        hours > 0 -> "${hours}小时"
        else -> "即将解锁"
    }
}
