package com.voicejournal.social

import com.voicejournal.data.model.Emotion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CommunityPost(
    val id: Long,
    val content: String,
    val emotion: Emotion,
    val timestamp: Long,
    val likes: Int = 0,
    val isAnonymous: Boolean = true
)

data class EmotionResonance(
    val emotion: Emotion,
    val count: Int,
    val percentage: Float
)

class SocialManager {

    private val _communityPosts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val communityPosts: StateFlow<List<CommunityPost>> = _communityPosts

    private val _emotionResonance = MutableStateFlow<List<EmotionResonance>>(emptyList())
    val emotionResonance: StateFlow<List<EmotionResonance>> = _emotionResonance

    init {
        loadMockData()
    }

    private fun loadMockData() {
        // 模拟社区数据
        _communityPosts.value = listOf(
            CommunityPost(
                id = 1,
                content = "今天终于完成了一个困扰我很久的项目，感觉如释重负！",
                emotion = Emotion.HAPPY,
                timestamp = System.currentTimeMillis() - 3600000,
                likes = 12
            ),
            CommunityPost(
                id = 2,
                content = "最近工作压力很大，但我在努力调整心态...",
                emotion = Emotion.ANXIOUS,
                timestamp = System.currentTimeMillis() - 7200000,
                likes = 8
            ),
            CommunityPost(
                id = 3,
                content = "周末去爬山了，大自然真的能治愈一切",
                emotion = Emotion.CALM,
                timestamp = System.currentTimeMillis() - 86400000,
                likes = 15
            )
        )

        // 模拟情绪共鸣数据
        _emotionResonance.value = listOf(
            EmotionResonance(Emotion.HAPPY, 120, 30f),
            EmotionResonance(Emotion.CALM, 95, 24f),
            EmotionResonance(Emotion.ANXIOUS, 80, 20f),
            EmotionResonance(Emotion.NEUTRAL, 60, 15f),
            EmotionResonance(Emotion.EXCITED, 30, 8f),
            EmotionResonance(Emotion.SAD, 15, 3f)
        )
    }

    fun shareToComm unity(content: String, emotion: Emotion) {
        val newPost = CommunityPost(
            id = System.currentTimeMillis(),
            content = content,
            emotion = emotion,
            timestamp = System.currentTimeMillis(),
            likes = 0
        )
        _communityPosts.value = listOf(newPost) + _communityPosts.value
    }

    fun likePost(postId: Long) {
        _communityPosts.value = _communityPosts.value.map { post ->
            if (post.id == postId) {
                post.copy(likes = post.likes + 1)
            } else {
                post
            }
        }
    }
}
