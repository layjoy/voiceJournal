package com.voicejournal.emotion

import com.voicejournal.data.model.Emotion

class EmotionAnalyzer {

    private val happyKeywords = listOf(
        "开心", "高兴", "快乐", "幸福", "愉快", "欢乐", "喜悦", "兴奋",
        "哈哈", "嘻嘻", "棒", "好", "赞", "爱", "喜欢", "满意"
    )

    private val sadKeywords = listOf(
        "难过", "伤心", "悲伤", "痛苦", "失望", "沮丧", "郁闷", "哭",
        "泪", "遗憾", "可惜", "糟糕", "不好", "失败", "孤独", "寂寞"
    )

    private val calmKeywords = listOf(
        "平静", "安静", "宁静", "放松", "舒服", "舒适", "淡定", "冷静",
        "平和", "安心", "轻松", "自在", "悠闲", "惬意"
    )

    private val excitedKeywords = listOf(
        "激动", "兴奋", "热血", "澎湃", "振奋", "亢奋", "狂喜", "疯狂",
        "太棒了", "太好了", "amazing", "awesome", "哇", "天啊"
    )

    private val anxiousKeywords = listOf(
        "焦虑", "紧张", "担心", "害怕", "恐惧", "不安", "忐忑", "慌",
        "压力", "烦躁", "烦恼", "困扰", "纠结", "迷茫"
    )

    fun analyzeEmotion(text: String): Emotion {
        if (text.isBlank()) {
            return Emotion.NEUTRAL
        }

        val scores = mutableMapOf(
            Emotion.HAPPY to 0,
            Emotion.SAD to 0,
            Emotion.CALM to 0,
            Emotion.EXCITED to 0,
            Emotion.ANXIOUS to 0
        )

        // 计算每种情绪的得分
        happyKeywords.forEach { keyword ->
            if (text.contains(keyword)) {
                scores[Emotion.HAPPY] = scores[Emotion.HAPPY]!! + 1
            }
        }

        sadKeywords.forEach { keyword ->
            if (text.contains(keyword)) {
                scores[Emotion.SAD] = scores[Emotion.SAD]!! + 1
            }
        }

        calmKeywords.forEach { keyword ->
            if (text.contains(keyword)) {
                scores[Emotion.CALM] = scores[Emotion.CALM]!! + 1
            }
        }

        excitedKeywords.forEach { keyword ->
            if (text.contains(keyword)) {
                scores[Emotion.EXCITED] = scores[Emotion.EXCITED]!! + 1
            }
        }

        anxiousKeywords.forEach { keyword ->
            if (text.contains(keyword)) {
                scores[Emotion.ANXIOUS] = scores[Emotion.ANXIOUS]!! + 1
            }
        }

        // 检查标点符号
        val exclamationCount = text.count { it == '!' || it == '！' }
        val questionCount = text.count { it == '?' || it == '？' }

        if (exclamationCount >= 2) {
            scores[Emotion.EXCITED] = scores[Emotion.EXCITED]!! + exclamationCount
        }

        if (questionCount >= 2) {
            scores[Emotion.ANXIOUS] = scores[Emotion.ANXIOUS]!! + questionCount / 2
        }

        // 返回得分最高的情绪
        val maxEmotion = scores.maxByOrNull { it.value }
        return if (maxEmotion != null && maxEmotion.value > 0) {
            maxEmotion.key
        } else {
            Emotion.NEUTRAL
        }
    }

    fun getEmotionDescription(emotion: Emotion): String {
        return when (emotion) {
            Emotion.HAPPY -> "开心"
            Emotion.SAD -> "难过"
            Emotion.CALM -> "平静"
            Emotion.EXCITED -> "兴奋"
            Emotion.ANXIOUS -> "焦虑"
            Emotion.NEUTRAL -> "平和"
        }
    }
}
