package com.voicejournal.ai

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.aiConfigDataStore: DataStore<Preferences> by preferencesDataStore(name = "ai_config")

class AIConfigManager(private val context: Context) {

    private object PreferencesKeys {
        val AI_PROVIDER = stringPreferencesKey("ai_provider")
        val API_KEY = stringPreferencesKey("api_key")
        val BASE_URL = stringPreferencesKey("base_url")
        val MODEL = stringPreferencesKey("model")
    }

    val aiConfig: Flow<AIConfig> = context.aiConfigDataStore.data.map { preferences ->
        val providerName = preferences[PreferencesKeys.AI_PROVIDER] ?: AIProvider.CLAUDE.name
        val provider = try {
            AIProvider.valueOf(providerName)
        } catch (e: IllegalArgumentException) {
            AIProvider.CLAUDE
        }

        AIConfig(
            provider = provider,
            apiKey = preferences[PreferencesKeys.API_KEY] ?: "",
            baseUrl = preferences[PreferencesKeys.BASE_URL] ?: "",
            model = preferences[PreferencesKeys.MODEL] ?: ""
        )
    }

    suspend fun setProvider(provider: AIProvider) {
        context.aiConfigDataStore.edit { preferences ->
            preferences[PreferencesKeys.AI_PROVIDER] = provider.name
            // 切换提供商时重置 URL 和模型为默认值
            preferences[PreferencesKeys.BASE_URL] = ""
            preferences[PreferencesKeys.MODEL] = ""
        }
    }

    suspend fun setApiKey(apiKey: String) {
        context.aiConfigDataStore.edit { preferences ->
            preferences[PreferencesKeys.API_KEY] = apiKey
        }
    }

    suspend fun setBaseUrl(baseUrl: String) {
        context.aiConfigDataStore.edit { preferences ->
            preferences[PreferencesKeys.BASE_URL] = baseUrl
        }
    }

    suspend fun setModel(model: String) {
        context.aiConfigDataStore.edit { preferences ->
            preferences[PreferencesKeys.MODEL] = model
        }
    }

    suspend fun clearConfig() {
        context.aiConfigDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
