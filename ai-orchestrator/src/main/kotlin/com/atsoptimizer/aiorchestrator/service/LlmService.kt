package com.atsoptimizer.aiorchestrator.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

@Service
class LlmService(
    @Value("\${llm.provider}") private val provider: String,
    @Value("\${llm.anthropic.api-key}") private val anthropicApiKey: String,
    @Value("\${llm.anthropic.model}") private val anthropicModel: String,
    @Value("\${llm.anthropic.api-url}") private val anthropicApiUrl: String,
    @Value("\${llm.openai.api-key}") private val openaiApiKey: String,
    @Value("\${llm.openai.model}") private val openaiModel: String,
    @Value("\${llm.openai.api-url}") private val openaiApiUrl: String
) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    /**
     * Analyze using configured LLM provider
     */
    suspend fun analyze(prompt: String): String = withContext(Dispatchers.IO) {
        logger.info { "LlmService: Sending request to $provider" }

        return@withContext when (provider.lowercase()) {
            "anthropic" -> callAnthropic(prompt)
            "openai" -> callOpenAi(prompt)
            else -> throw IllegalArgumentException("Unsupported LLM provider: $provider")
        }
    }

    /**
     * Call Anthropic Claude API
     */
    private fun callAnthropic(prompt: String): String {
        if (anthropicApiKey.isEmpty()) {
            throw IllegalStateException("Anthropic API key not configured")
        }

        val requestBody = mapOf(
            "model" to anthropicModel,
            "max_tokens" to 1024,
            "messages" to listOf(
                mapOf(
                    "role" to "user",
                    "content" to prompt
                )
            )
        )

        val jsonBody = objectMapper.writeValueAsString(requestBody)

        val request = Request.Builder()
            .url(anthropicApiUrl)
            .addHeader("x-api-key", anthropicApiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .addHeader("content-type", "application/json")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                logger.error { "Anthropic API error: ${response.code} - ${response.body?.string()}" }
                throw RuntimeException("Anthropic API call failed: ${response.code}")
            }

            val responseBody = response.body?.string()
                ?: throw RuntimeException("Empty response from Anthropic")

            logger.debug { "Anthropic response: $responseBody" }

            // Parse response
            val jsonResponse = objectMapper.readTree(responseBody)
            return jsonResponse.get("content")
                ?.get(0)
                ?.get("text")
                ?.asText()
                ?: throw RuntimeException("Failed to parse Anthropic response")
        }
    }

    /**
     * Call OpenAI API
     */
    private fun callOpenAi(prompt: String): String {
        if (openaiApiKey.isEmpty()) {
            throw IllegalStateException("OpenAI API key not configured")
        }

        val requestBody = mapOf(
            "model" to openaiModel,
            "messages" to listOf(
                mapOf(
                    "role" to "user",
                    "content" to prompt
                )
            ),
            "max_tokens" to 1024,
            "temperature" to 0.7
        )

        val jsonBody = objectMapper.writeValueAsString(requestBody)

        val request = Request.Builder()
            .url(openaiApiUrl)
            .addHeader("Authorization", "Bearer $openaiApiKey")
            .addHeader("Content-Type", "application/json")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                logger.error { "OpenAI API error: ${response.code} - ${response.body?.string()}" }
                throw RuntimeException("OpenAI API call failed: ${response.code}")
            }

            val responseBody = response.body?.string()
                ?: throw RuntimeException("Empty response from OpenAI")

            logger.debug { "OpenAI response: $responseBody" }

            // Parse response
            val jsonResponse = objectMapper.readTree(responseBody)
            return jsonResponse.get("choices")
                ?.get(0)
                ?.get("message")
                ?.get("content")
                ?.asText()
                ?: throw RuntimeException("Failed to parse OpenAI response")
        }
    }
}