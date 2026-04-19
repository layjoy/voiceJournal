package com.voicejournal

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.voicejournal.ui.theme.VoiceJournalTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VoiceJournalAppTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAppLaunches() {
        composeTestRule.setContent {
            VoiceJournalTheme {
                VoiceJournalApp()
            }
        }

        composeTestRule.onNodeWithText("声音日记").assertExists()
    }

    @Test
    fun testNavigationToTimeline() {
        composeTestRule.setContent {
            VoiceJournalTheme {
                VoiceJournalApp()
            }
        }

        composeTestRule.onNodeWithContentDescription("时间轴").performClick()
        composeTestRule.onNodeWithText("时间轴").assertExists()
    }

    @Test
    fun testNavigationToCapsule() {
        composeTestRule.setContent {
            VoiceJournalTheme {
                VoiceJournalApp()
            }
        }

        composeTestRule.onNodeWithContentDescription("时光胶囊").performClick()
        composeTestRule.onNodeWithText("时光胶囊").assertExists()
    }

    @Test
    fun testRecordButtonExists() {
        composeTestRule.setContent {
            VoiceJournalTheme {
                VoiceJournalApp()
            }
        }

        composeTestRule.onNodeWithContentDescription("录音").assertExists()
    }
}
