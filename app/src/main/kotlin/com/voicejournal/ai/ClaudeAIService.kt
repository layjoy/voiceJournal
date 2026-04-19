package com.voicejournal.ai

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Claude AI分析服务
 * 提供深度情绪分析、日记洞察、写作建议等功能
 */
class ClaudeAIService(private val context: Context) {

    companion object {
        private const val TAG = "ClaudeAI"
        private const val API_URL = "https://api.anthropic.com/v1/messages"
        private const val MODEL = "claude-3-5-sonnet-20241022"

        // 需要用户配置自己的API Key
        private const val API_KEY = "YOUR_ANTHROPIC_API_KEY"
    }

    /**
     * 深度情绪分析
     * 分析日记内容的情绪细节、情绪变化趋势
     */
    suspend fun analyzeEmotionDeep(text: String): EmotionAnalysisResult {
        val prompt = """
            请深度分析以下日记内容的情绪：

            "$text"

            请从以下维度分析：
            1. 主要情绪及强度（0-10分）
            2. 次要情绪
            3. 情绪变化趋势
            4. 情绪触发因素
            5. 积极/消极情绪比例

            以JSON格式返回：
            {
                "primary_emotion": "主要情绪",
                "intensity": 8,
                "secondary_emotions": ["次要情绪1", "次要情绪2"],
                "trend": "上升/下降/稳定",
                "triggers": ["触发因素1", "触发因素2"],
                "positive_ratio": 0.7,
                "analysis": "详细分析"
            }
        """.trimIndent()

        return try {
            val response = callClaudeAPI(prompt)
            parseEmotionAnalysis(response)
        } catch (e: Exception) {
            Log.e(TAG, "Emotion analysis failed", e)
            EmotionAnalysisResult.error("分析失败: ${e.message}")
        }
    }

    /**
     * 日记洞察生成
     * 从日记中提取关键洞察、模式和建议
     */
    suspend fun generateInsights(entries: List<String>): InsightResult {
        val prompt = """
            请分析以下多条日记，提取关键洞察：

            ${entries.joinToString("\n\n") { "- $it" }}

            请提供：
            1. 情绪模式（如：工作日焦虑，周末放松）
            2. 关注主题（如：工作、家庭、健康）
            3. 积极变化
            4. 需要关注的问题
            5. 个性化建议

            以JSON格式返回：
            {
                "patterns": ["模式1", "模式2"],
                "themes": ["主题1", "主题2"],
                "positive_changes": ["变化1"],
                "concerns": ["问题1"],
                "suggestions": ["建议1", "建议2"]
            }
        """.trimIndent()

        return try {
            val response = callClaudeAPI(prompt)
            parseInsights(response)
        } catch (e: Exception) {
            Log.e(TAG, "Insights generation failed", e)
            InsightResult.error("生成失败: ${e.message}")
        }
    }

    /**
     * 写作建议
     * 提供日记写作的改进建议
     */
    suspend fun getWritingSuggestions(text: String): WritingSuggestionResult {
        val prompt = """
            请为以下日记内容提供写作建议：

            "$text"

            请提供：
            1. 表达清晰度评分（0-10）
            2. 情感表达评分（0-10）
            3. 具体改进建议
            4. 可以添加的细节
            5. 更好的表达方式示例

            以JSON格式返回：
            {
                "clarity_score": 8,
                "emotion_score": 7,
                "suggestions": ["建议1", "建议2"],
                "details_to_add": ["细节1"],
                "better_expressions": ["原文 -> 改进后"]
            }
        """.trimIndent()

        return try {
            val response = callClaudeAPI(prompt)
            parseWritingSuggestions(response)
        } catch (e: Exception) {
            Log.e(TAG, "Writing suggestions failed", e)
            WritingSuggestionResult.error("生成失败: ${e.message}")
        }
    }

    /**
     * 情绪趋势预测
     * 基于历史数据预测未来情绪趋势
     */
    suspend fun predictEmotionTrend(recentEntries: List<Pair<String, String>>): TrendPredictionResult {
        val prompt = """
            请分析以下时间序列的日记数据，预测情绪趋势：

            ${recentEntries.joinToString("\n") { (date, text) -> "$date: $text" }}

            请提供：
            1. 未来3天情绪趋势预测
            2. 预测依据
            3. 风险提示
            4. 改善建议

            以JSON格式返回：
            {
                "trend": "上升/下降/稳定",
                "confidence": 0.8,
                "prediction": "预测描述",
                "basis": ["依据1", "依据2"],
                "risks": ["风险1"],
                "recommendations": ["建议1"]
            }
        """.trimIndent()

        return try {
            val response = callClaudeAPI(prompt)
            parseTrendPrediction(response)
        } catch (e: Exception) {
            Log.e(TAG, "Trend prediction failed", e)
            TrendPredictionResult.error("预测失败: ${e.message}")
        }
    }

    /**
     * 智能摘要生成
     * 为长日记生成简洁摘要
     */
    suspend fun generateSummary(text: String, maxLength: Int = 50): String {
        val prompt = """
            请为以下日记生成一句话摘要（不超过${maxLength}字）：

            "$text"

            要求：
            1. 抓住核心内容
            2. 保留情绪色彩
            3. 简洁有力

            只返回摘要文本，不要其他内容。
        """.trimIndent()

        return try {
            callClaudeAPI(prompt)
        } catch (e: Exception) {
            Log.e(TAG, "Summary generation failed", e)
            "摘要生成失败"
        }
    }

    /**
     * 相似日记推荐
     * 找出情绪或主题相似的历史日记
     */
    suspend fun findSimilarEntries(
        currentText: String,
        historicalEntries: List<Pair<String, String>>
    ): List<SimilarEntry> {
        val prompt = """
            当前日记：
            "$currentText"

            历史日记：
            ${historicalEntries.mapIndexed { index, (date, text) ->
                "$index. [$date] $text"
            }.joinToString("\n")}

            请找出与当前日记情绪或主题最相似的3条历史日记，并说明相似原因。

            以JSON格式返回：
            {
                "similar_entries": [
                    {
                        "index": 0,
                        "similarity_score": 0.85,
                        "reason": "相似原因"
                    }
                ]
            }
        """.trimIndent()

        return try {
            val response = callClaudeAPI(prompt)
            parseSimilarEntries(response, historicalEntries)
        } catch (e: Exception) {
            Log.e(TAG, "Similar entries search failed", e)
            emptyList()
        }
    }

    /**
     * 调用Claude API
     */
    private suspend fun callClaudeAPI(prompt: String): String = withContext(Dispatchers.IO) {
        val url = URL(API_URL)
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("x-api-key", API_KEY)
            connection.setRequestProperty("anthropic-version", "2023-06-01")
            connection.doOutput = true

            val requestBody = JSONObject().apply {
                put("model", MODEL)
                put("max_tokens", 2048)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(requestBody.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                    val response = reader.readText()
                    val jsonResponse = JSONObject(response)
                    val content = jsonResponse.getJSONArray("content")
                    content.getJSONObject(0).getString("text")
                }
            } else {
                throw Exception("API call failed with code: $responseCode")
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun parseEmotionAnalysis(response: String): EmotionAnalysisResult {
        return try {
            val json = JSONObject(response)
            EmotionAnalysisResult(
                primaryEmotion = json.getString("primary_emotion"),
                intensity = json.getInt("intensity"),
                secondaryEmotions = json.getJSONArray("secondary_emotions").let { array ->
                    List(array.length()) { array.getString(it) }
                },
                trend = json.getString("trend"),
                triggers = json.getJSONArray("triggers").let { array ->
                    List(array.length()) { array.getString(it) }
                },
                positiveRatio = json.getDouble("positive_ratio").toFloat(),
                analysis = json.getString("analysis")
            )
        } catch (e: Exception) {
            EmotionAnalysisResult.error("解析失败")
        }
    }

    private fun parseInsights(response: String): InsightResult {
        return try {
            val json = JSONObject(response)
            InsightResult(
                patterns = json.getJSONArray("patterns").toStringList(),
                themes = json.getJSONArray("themes").toStringList(),
                positiveChanges = json.getJSONArray("positive_changes").toStringList(),
                concerns = json.getJSONArray("concerns").toStringList(),
                suggestions = json.getJSONArray("suggestions").toStringList()
            )
        } catch (e: Exception) {
            InsightResult.error("解析失败")
        }
    }

    private fun parseWritingSuggestions(response: String): WritingSuggestionResult {
        return try {
            val json = JSONObject(response)
            WritingSuggestionResult(
                clarityScore = json.getInt("clarity_score"),
                emotionScore = json.getInt("emotion_score"),
                suggestions = json.getJSONArray("suggestions").toStringList(),
                detailsToAdd = json.getJSONArray("details_to_add").toStringList(),
                betterExpressions = json.getJSONArray("better_expressions").toStringList()
            )
        } catch (e: Exception) {
            WritingSuggestionResult.error("解析失败")
        }
    }

    private fun parseTrendPrediction(response: String): TrendPredictionResult {
        return try {
            val json = JSONObject(response)
            TrendPredictionResult(
                trend = json.getString("trend"),
                confidence = json.getDouble("confidence").toFloat(),
                prediction = json.getString("prediction"),
                basis = json.getJSONArray("basis").toStringList(),
                risks = json.getJSONArray("risks").toStringList(),
                recommendations = json.getJSONArray("recommendations").toStringList()
            )
        } catch (e: Exception) {
            TrendPredictionResult.error("解析失败")
        }
    }

    private fun parseSimilarEntries(
        response: String,
        historicalEntries: List<Pair<String, String>>
    ): List<SimilarEntry> {
        return try {
            val json = JSONObject(response)
            val array = json.getJSONArray("similar_entries")
            List(array.length()) { i ->
                val item = array.getJSONObject(i)
                val index = item.getInt("index")
                SimilarEntry(
                    date = historicalEntries[index].first,
                    text = historicalEntries[index].second,
                    similarityScore = item.getDouble("similarity_score").toFloat(),
                    reason = item.getString("reason")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun JSONArray.toStringList(): List<String> {
        return List(length()) { getString(it) }
    }
}

// 数据类定义
data class EmotionAnalysisResult(
    val primaryEmotion: String,
    val intensity: Int,
    val secondaryEmotions: List<String>,
    val trend: String,
    val triggers: List<String>,
    val positiveRatio: Float,
    val analysis: String,
    val isError: Boolean = false,
    val errorMessage: String = ""
) {
    companion object {
        fun error(message: String) = EmotionAnalysisResult(
            "", 0, emptyList(), "", emptyList(), 0f, "",
            isError = true, errorMessage = message
        )
    }
}

data class InsightResult(
    val patterns: List<String>,
    val themes: List<String>,
    val positiveChanges: List<String>,
    val concerns: List<String>,
    val suggestions: List<String>,
    val isError: Boolean = false,
    val errorMessage: String = ""
) {
    companion object {
        fun error(message: String) = InsightResult(
            emptyList(), emptyList(), emptyList(), emptyList(), emptyList(),
            isError = true, errorMessage = message
        )
    }
}

data class WritingSuggestionResult(
    val clarityScore: Int,
    val emotionScore: Int,
    val suggestions: List<String>,
    val detailsToAdd: List<String>,
    val betterExpressions: List<String>,
    val isError: Boolean = false,
    val errorMessage: String = ""
) {
    companion object {
        fun error(message: String) = WritingSuggestionResult(
            0, 0, emptyList(), emptyList(), emptyList(),
            isError = true, errorMessage = message
        )
    }
}

data class TrendPredictionResult(
    val trend: String,
    val confidence: Float,
    val prediction: String,
    val basis: List<String>,
    val risks: List<String>,
    val recommendations: List<String>,
    val isError: Boolean = false,
    val errorMessage: String = ""
) {
    companion object {
        fun error(message: String) = TrendPredictionResult(
            "", 0f, "", emptyList(), emptyList(), emptyList(),
            isError = true, errorMessage = message
        )
    }
}

data class SimilarEntry(
    val date: String,
    val text: String,
    val similarityScore: Float,
    val reason: String
)
