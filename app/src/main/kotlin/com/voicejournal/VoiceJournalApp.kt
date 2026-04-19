package com.voicejournal

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.voicejournal.ui.components.BottomNavigationBar
import com.voicejournal.ui.screens.*
import com.voicejournal.viewmodel.*

@Composable
fun VoiceJournalApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "record"

    // 需要显示底部导航栏的路由
    val bottomNavRoutes = listOf("record", "timeline", "calendar", "analytics", "settings")
    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("record") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "record",
            modifier = Modifier.padding(paddingValues)
        ) {
            // 录音界面
            composable("record") {
                val viewModel: RecordViewModel = viewModel()
                RecordScreen(
                    viewModel = viewModel,
                    onNavigateToTimeline = { navController.navigate("timeline") },
                    onNavigateToCapsule = { navController.navigate("capsule") },
                    onNavigateToAIInsights = { navController.navigate("ai_insights") }
                )
            }

            // 时间轴
            composable("timeline") {
                val viewModel: TimelineViewModel = viewModel()
                TimelineScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAnalysis = { entryId ->
                        navController.navigate("ai_analysis/$entryId")
                    }
                )
            }

            // 时光胶囊
            composable("capsule") {
                val viewModel: CapsuleViewModel = viewModel()
                CapsuleScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 情绪日历
            composable("calendar") {
                val viewModel: EmotionCalendarViewModel = viewModel()
                EmotionCalendarScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onDateClick = { timestamp ->
                        // 可以导航到该日期的详细记录
                    }
                )
            }

            // 搜索
            composable("search") {
                val viewModel: SearchViewModel = viewModel()
                SearchScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onEntryClick = { entryId ->
                        navController.navigate("ai_analysis/$entryId")
                    }
                )
            }

            // AI 洞察
            composable("ai_insights") {
                val viewModel: AIInsightsViewModel = viewModel()
                AIInsightsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // AI 分析详情
            composable(
                route = "ai_analysis/{entryId}",
                arguments = listOf(navArgument("entryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val entryId = backStackEntry.arguments?.getString("entryId") ?: ""
                val viewModel: AIAnalysisViewModel = viewModel()
                AIAnalysisScreen(
                    entryId = entryId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // AI 聊天
            composable("ai_chat") {
                val viewModel: AIChatViewModel = viewModel()
                AIChatScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 数据分析
            composable("analytics") {
                val viewModel: AnalyticsViewModel = viewModel()
                AnalyticsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 设置
            composable("settings") {
                val viewModel: SettingsViewModel = viewModel()
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToBackup = { navController.navigate("backup") },
                    onNavigateToExport = { navController.navigate("export") }
                )
            }

            // 备份与恢复
            composable("backup") {
                val viewModel: BackupViewModel = viewModel()
                BackupScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 导出数据
            composable("export") {
                val viewModel: ExportViewModel = viewModel()
                ExportScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 标签管理
            composable("tags") {
                val viewModel: TagViewModel = viewModel()
                TagManagementScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 社交分享
            composable("social") {
                val viewModel: SocialViewModel = viewModel()
                SocialScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
