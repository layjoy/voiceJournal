package com.voicejournal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TagChip(
    tag: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "tag")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val backgroundColor = if (isSelected) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF6C63FF),
                Color(0xFF5A52E0)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF2A2A3E),
                Color(0xFF1F1F2E)
            )
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "#$tag",
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun TagCloud(
    tags: List<String>,
    selectedTags: List<String>,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "标签云",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 使用FlowRow布局标签
        var currentRow = mutableListOf<String>()
        val rows = mutableListOf<List<String>>()

        tags.forEach { tag ->
            currentRow.add(tag)
            if (currentRow.size >= 3) {
                rows.add(currentRow.toList())
                currentRow = mutableListOf()
            }
        }
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
        }

        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { tag ->
                    TagChip(
                        tag = tag,
                        isSelected = selectedTags.contains(tag),
                        onClick = { onTagClick(tag) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
