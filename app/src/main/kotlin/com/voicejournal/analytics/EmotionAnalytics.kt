package com.voicejournal.analytics

import com.voicejournal.data.model.Emotion
import com.voicejournal.data.model.JournalEntry
import java.util.*
import kotlin.math.abs

class EmotionAnalytics {

    data class EmotionTrend(
        val emotion: Emotion,
        val count: Int,
        val percentage: Float,
        val trend: String // "上升", "下降", "稳定"
    )

    data class TimePattern(
        val hour: Int,
        val dominantEmotion: Emotion,
        val count: Int
    )

    data class WeeklyReport(
        val weekStart: Long,
        val weekEnd: Long,
        val totalEntries: Int,
        val dominantEmotion: Emotion,
        val emotionDistribution: Map<Emotion, Int>,
        val averageMood: Float,
        val insights: List<String>
    )

    fun analyzeEmotionTrends(entries: List<JournalEntry>, days: Int = 30): List<EmotionTrend> {
        val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        val recentEntries = entries.filter { it.timestamp >= cutoffTime }

        val emotionCounts = recentEntries.groupingBy { it.emotion }.eachCount()
        val total = recentEntries.size.toFloat()

        return emotionCounts.map { (emotion, count) ->
            val percentage = (count / total) * 100
            val trend = calculateTrend(entries, emotion, days)
            EmotionTrend(emotion, count, percentage, trend)
        }.sortedByDescending { it.count }
    }

    private fun calculateTrend(entries: List<JournalEntry>, emotion: Emotion, days: Int): String {
        val midPoint = System.currentTimeMillis() - (days / 2 * 24 * 60 * 60 * 1000L)

        val firstHalf = entries.filter { it.timestamp < midPoint && it.emotion == emotion }.size
        val secondHalf = entries.filter { it.timestamp >= midPoint && it.emotion == emotion }.size

        return when {
            secondHalf > firstHalf * 1.2 -> "上升"
            secondHalf < firstHalf * 0.8 -> "下降"
            else -> "稳定"
        }
    }

    fun analyzeTimePatterns(entries: List<JournalEntry>): List<TimePattern> {
        val calendar = Calendar.getInstance()
        val hourlyData = mutableMapOf<Int, MutableList<Emotion>>()

        entries.forEach { entry ->
            calendar.timeInMillis = entry.timestamp
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            hourlyData.getOrPut(hour) { mutableListOf() }.add(entry.emotion)
        }

        return hourlyData.map { (hour, emotions) ->
            val dominantEmotion = emotions.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: Emotion.NEUTRAL
            TimePattern(hour, dominantEmotion, emotions.size)
        }.sortedBy { it.hour }
    }

    fun generateWeeklyReport(entries: List<JournalEntry>): WeeklyReport {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val weekStart = calendar.timeInMillis

        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        val weekEnd = calendar.timeInMillis

        val weekEntries = entries.filter { it.timestamp in weekStart until weekEnd }
        val emotionDistribution = weekEntries.groupingBy { it.emotion }.eachCount()
        val dominantEmotion = emotionDistribution.maxByOrNull { it.value }?.key ?: Emotion.NEUTRAL

        val averageMood = calculateAverageMood(weekEntries)
        val insights = generateInsights(weekEntries, emotionDistribution)

        return WeeklyReport(
            weekStart = weekStart,
            weekEnd = weekEnd,
            totalEntries = weekEntries.size,
            dominantEmotion = dominantEmotion,
            emotionDistribution = emotionDistribution,
            averageMood = averageMood,
            insights = insights
        )
    }

    private fun calculateAverageMood(entries: List<JournalEntry>): Float {
        if (entries.isEmpty()) return 0.5f

        val moodScores = entries.map { entry ->
            when (entry.emotion) {
                Emotion.HAPPY -> 1.0f
                Emotion.EXCITED -> 0.9f
                Emotion.CALM -> 0.7f
                Emotion.NEUTRAL -> 0.5f
                Emotion.ANXIOUS -> 0.3f
                Emotion.SAD -> 0.1f
            }
        }

        return moodScores.average().toFloat()
    }

    private fun generateInsights(entries: List<JournalEntry>, distribution: Map<Emotion, Int>): List<String> {
        val insights = mutableListOf<String>()

        // 记录频率洞察
        when {
            entries.size >= 7 -> insights.add("本周记录很规律，保持下去！")
            entries.size >= 4 -> insights.add("本周记录不错，可以更频繁一些")
            else -> insights.add("本周记录较少，建议增加记录频率")
        }

        // 情绪洞察
        val positiveCount = (distribution[Emotion.HAPPY] ?: 0) + (distribution[Emotion.EXCITED] ?: 0)
        val negativeCount = (distribution[Emotion.SAD] ?: 0) + (distribution[Emotion.ANXIOUS] ?: 0)

        when {
            positiveCount > negativeCount * 2 -> insights.add("本周情绪积极，继续保持！")
            negativeCount > positiveCount -> insights.add("本周情绪波动较大，注意调节")
            else -> insights.add("本周情绪较为平稳")
        }

        return insights
    }

    fun findEmotionCorrelations(entries: List<JournalEntry>): Map<String, Float> {
        // 分析情绪之间的关联性
        val correlations = mutableMapOf<String, Float>()

        // 简单的时间相关性分析
        val sortedEntries = entries.sortedBy { it.timestamp }
        for (i in 0 until sortedEntries.size - 1) {
            val current = sortedEntries[i]
            val next = sortedEntries[i + 1]

            val timeDiff = abs(next.timestamp - current.timestamp)
            if (timeDiff < 24 * 60 * 60 * 1000) { // 24小时内
                val key = "${current.emotion.name}->${next.emotion.name}"
                correlations[key] = correlations.getOrDefault(key, 0f) + 1f
            }
        }

        return correlations
    }
}
