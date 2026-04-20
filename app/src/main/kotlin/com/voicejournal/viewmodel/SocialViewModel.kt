package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.voicejournal.social.CommunityPost
import com.voicejournal.social.EmotionResonance
import com.voicejournal.social.SocialManager
import com.voicejournal.social.SocialPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SocialViewModel(application: Application) : AndroidViewModel(application) {

    private val socialManager = SocialManager()

    private val _isSharing = MutableStateFlow(false)
    val isSharing: StateFlow<Boolean> = _isSharing

    val communityPosts: StateFlow<List<CommunityPost>> = socialManager.communityPosts
    val emotionResonance: StateFlow<List<EmotionResonance>> = socialManager.emotionResonance

    fun likePost(postId: Long) {
        socialManager.likePost(postId)
    }

    fun shareToSocial(platform: SocialPlatform) {
        _isSharing.value = true
        // TODO: Implement actual social sharing
        _isSharing.value = false
    }
}
