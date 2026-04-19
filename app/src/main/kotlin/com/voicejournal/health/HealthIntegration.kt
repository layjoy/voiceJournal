package com.voicejournal.health

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

data class HealthData(
    val sleepHours: Float = 0f,
    val steps: Int = 0,
    val heartRate: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

class HealthIntegration(private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHealthData(): HealthData {
        // 集成Google Fit或Health Connect API
        // 当前返回模拟数据，实际项目中需要：
        // 1. 添加 Google Fit 或 Health Connect 依赖
        // 2. 请求健康数据权限
        // 3. 调用相应 API 获取真实数据
        return HealthData(
            sleepHours = 7.5f,
            steps = 8000,
            heartRate = 72
        )
    }

    fun analyzeHealthImpact(healthData: HealthData, emotionScore: Float): String {
        val insights = mutableListOf<String>()

        // 睡眠分析
        when {
            healthData.sleepHours < 6 -> insights.add("睡眠不足可能影响情绪")
            healthData.sleepHours > 9 -> insights.add("睡眠过多可能是情绪低落的信号")
            else -> insights.add("睡眠质量良好")
        }

        // 运动分析
        when {
            healthData.steps < 3000 -> insights.add("建议增加运动量改善情绪")
            healthData.steps > 10000 -> insights.add("运动量充足，有助于情绪稳定")
        }

        // 心率分析
        when {
            healthData.heartRate > 100 -> insights.add("心率偏高，可能处于焦虑状态")
            healthData.heartRate < 60 -> insights.add("心率平稳，情绪较为放松")
        }

        return insights.joinToString("\n")
    }
}
