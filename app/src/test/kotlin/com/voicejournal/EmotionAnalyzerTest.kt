package com.voicejournal

import com.voicejournal.emotion.EmotionAnalyzer
import com.voicejournal.data.model.Emotion
import org.junit.Test
import org.junit.Assert.*

class EmotionAnalyzerTest {

    private val analyzer = EmotionAnalyzer()

    @Test
    fun testHappyEmotion() {
        val text = "今天真的太开心了！遇到了好多有趣的事情，感觉很幸福"
        val emotion = analyzer.analyzeEmotion(text)
        assertEquals(Emotion.HAPPY, emotion)
    }

    @Test
    fun testSadEmotion() {
        val text = "今天很难过，事情没有按照预期发展，感觉很失望"
        val emotion = analyzer.analyzeEmotion(text)
        assertEquals(Emotion.SAD, emotion)
    }

    @Test
    fun testCalmEmotion() {
        val text = "今天很平静，在家里放松地看了一本书，感觉很舒适"
        val emotion = analyzer.analyzeEmotion(text)
        assertEquals(Emotion.CALM, emotion)
    }

    @Test
    fun testExcitedEmotion() {
        val text = "太棒了！！！今天的表现超出预期，真是太激动了！"
        val emotion = analyzer.analyzeEmotion(text)
        assertEquals(Emotion.EXCITED, emotion)
    }

    @Test
    fun testAnxiousEmotion() {
        val text = "明天的考试让我很焦虑，担心准备不充分，感觉压力很大"
        val emotion = analyzer.analyzeEmotion(text)
        assertEquals(Emotion.ANXIOUS, emotion)
    }

    @Test
    fun testNeutralEmotion() {
        val text = "今天去了超市买了一些东西"
        val emotion = analyzer.analyzeEmotion(text)
        assertEquals(Emotion.NEUTRAL, emotion)
    }

    @Test
    fun testEmptyText() {
        val emotion = analyzer.analyzeEmotion("")
        assertEquals(Emotion.NEUTRAL, emotion)
    }

    @Test
    fun testEmotionDescription() {
        assertEquals("开心", analyzer.getEmotionDescription(Emotion.HAPPY))
        assertEquals("难过", analyzer.getEmotionDescription(Emotion.SAD))
        assertEquals("平静", analyzer.getEmotionDescription(Emotion.CALM))
        assertEquals("兴奋", analyzer.getEmotionDescription(Emotion.EXCITED))
        assertEquals("焦虑", analyzer.getEmotionDescription(Emotion.ANXIOUS))
        assertEquals("平和", analyzer.getEmotionDescription(Emotion.NEUTRAL))
    }
}
