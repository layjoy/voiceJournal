package com.voicejournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.voicejournal.social.CommunityPost
import com.voicejournal.social.EmotionResonance
import com.voicejournal.social.SocialManager
import kotlinx.coroutines.flow.StateFlow

class SocialViewModel(application: Application) : AndroidViewModel(application) {

    private val socialManager = SocialManager()

    val communityPosts: StateFlow<List<CommunityPost>> = socialManager.communityPosts
    val emotionResonance: StateFlow<List<EmotionResonance>> = socialManager.emotionResonance

    fun likePost(postId: Long) {
        socialManager.likePost(postId)
    }
}
