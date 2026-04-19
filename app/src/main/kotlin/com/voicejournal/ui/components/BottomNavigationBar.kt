package com.voicejournal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF1A1F3A).copy(alpha = 0.95f),
        contentColor = Color.White,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            icon = {
                NavIcon(
                    icon = Icons.Default.Mic,
                    label = "录音",
                    selected = currentRoute == "record"
                )
            },
            label = { Text("录音", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute == "record",
            onClick = { onNavigate("record") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF6C63FF),
                selectedTextColor = Color(0xFF6C63FF),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFF6C63FF).copy(alpha = 0.2f)
            )
        )

        NavigationBarItem(
            icon = {
                NavIcon(
                    icon = Icons.Default.Timeline,
                    label = "时间轴",
                    selected = currentRoute == "timeline"
                )
            },
            label = { Text("时间轴", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute == "timeline",
            onClick = { onNavigate("timeline") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF6C63FF),
                selectedTextColor = Color(0xFF6C63FF),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFF6C63FF).copy(alpha = 0.2f)
            )
        )

        NavigationBarItem(
            icon = {
                NavIcon(
                    icon = Icons.Default.CalendarMonth,
                    label = "日历",
                    selected = currentRoute == "calendar"
                )
            },
            label = { Text("日历", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute == "calendar",
            onClick = { onNavigate("calendar") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF6C63FF),
                selectedTextColor = Color(0xFF6C63FF),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFF6C63FF).copy(alpha = 0.2f)
            )
        )

        NavigationBarItem(
            icon = {
                NavIcon(
                    icon = Icons.Default.BarChart,
                    label = "分析",
                    selected = currentRoute == "analytics"
                )
            },
            label = { Text("分析", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute == "analytics",
            onClick = { onNavigate("analytics") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF6C63FF),
                selectedTextColor = Color(0xFF6C63FF),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFF6C63FF).copy(alpha = 0.2f)
            )
        )

        NavigationBarItem(
            icon = {
                NavIcon(
                    icon = Icons.Default.Settings,
                    label = "设置",
                    selected = currentRoute == "settings"
                )
            },
            label = { Text("设置", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute == "settings",
            onClick = { onNavigate("settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF6C63FF),
                selectedTextColor = Color(0xFF6C63FF),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFF6C63FF).copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
fun NavIcon(
    icon: ImageVector,
    label: String,
    selected: Boolean
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(32.dp)
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6C63FF).copy(alpha = 0.2f))
            )
        }
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
    }
}
