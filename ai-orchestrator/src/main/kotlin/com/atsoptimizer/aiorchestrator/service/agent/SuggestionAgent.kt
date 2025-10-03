package com.atsoptimizer.aiorchestrator.service.agent

import com.atsoptimizer.aiorchestrator.model.AgentResult
import com.atsoptimizer.aiorchestrator.service.LlmService
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

@Component
class SuggestionAgent(
    private val llmService: LlmService
) {

    /**
     * Uses LLM to generate intelligent suggestions
     */
    fun analyze(
        resumeText: String,
        jobTitle: String,
        jobDescription: String,
        missingKeywords: List<String>
    ): AgentResult = runBlocking {
        logger.info { "SuggestionAgent: Starting LLM-based analysis" }

        var score = 75 // Base score
        val findings = mutableListOf<String>()
        val suggestions = mutableListOf<String>()

        val executionTime = measureTimeMillis {
            try {
                val prompt = buildPrompt(resumeText, jobTitle, jobDescription, missingKeywords)
                val llmResponse = llmService.analyze(prompt)

                // Parse LLM response
                val parsedSuggestions = parseLlmResponse(llmResponse)
                suggestions.addAll(parsedSuggestions)

                // Adjust score based on LLM analysis
                score = calculateLlmScore(llmResponse)

                findings.add("AI analysis completed successfully")
                findings.add("Generated ${parsedSuggestions.size} personalized recommendations")

            } catch (e: Exception) {
                logger.error(e) { "Error in LLM analysis" }
                findings.add("LLM analysis unavailable - using rule-based suggestions")
                suggestions.addAll(getFallbackSuggestions(missingKeywords))
            }
        }

        logger.info { "SuggestionAgent: Completed with score $score" }

        AgentResult(
            agentName = "SuggestionAgent",
            score = score,
            findings = findings,
            suggestions = suggestions,
            executionTimeMs = executionTime
        )
    }

    private fun buildPrompt(
        resumeText: String,
        jobTitle: String,
        jobDescription: String,
        missingKeywords: List<String>
    ): String {
        return """
You are an expert resume reviewer helping a candidate optimize their resume for an ATS system.

JOB TITLE: $jobTitle

JOB DESCRIPTION:
${jobDescription.take(1000)}

CANDIDATE'S RESUME:
${resumeText.take(2000)}

MISSING KEYWORDS: ${missingKeywords.take(10).joinToString(", ")}

Please provide:
1. 3-5 specific, actionable suggestions to improve the resume for this job
2. Rate the overall resume quality from 0-100
3. Identify the top 3 strengths
4. Identify the top 3 weaknesses

Format your response as:
SCORE: [number]
STRENGTHS:
- [strength 1]
- [strength 2]
- [strength 3]
WEAKNESSES:
- [weakness 1]
- [weakness 2]
- [weakness 3]
SUGGESTIONS:
- [suggestion 1]
- [suggestion 2]
- [suggestion 3]
        """.trimIndent()
    }

    private fun parseLlmResponse(response: String): List<String> {
        val suggestions = mutableListOf<String>()

        try {
            // Extract suggestions section
            val suggestionsSection = response.substringAfter("SUGGESTIONS:", "")
            if (suggestionsSection.isNotEmpty()) {
                suggestions.addAll(
                    suggestionsSection.split("\n")
                        .filter { it.trim().startsWith("-") }
                        .map { it.trim().removePrefix("-").trim() }
                        .filter { it.isNotEmpty() }
                )
            }
        } catch (e: Exception) {
            logger.warn(e) { "Error parsing LLM response" }
        }

        return suggestions.ifEmpty {
            listOf("Review your resume against the job description carefully")
        }
    }

    private fun calculateLlmScore(response: String): Int {
        return try {
            val scoreText = response.substringAfter("SCORE:", "")
                .substringBefore("\n")
                .trim()
            scoreText.toIntOrNull() ?: 75
        } catch (e: Exception) {
            logger.warn(e) { "Error extracting score from LLM response" }
            75
        }
    }

    private fun getFallbackSuggestions(missingKeywords: List<String>): List<String> {
        return listOf(
            "Incorporate these missing keywords naturally: ${missingKeywords.take(5).joinToString(", ")}",
            "Tailor your experience section to highlight relevant achievements for this role",
            "Add quantifiable metrics to demonstrate your impact (e.g., percentages, dollar amounts)",
            "Ensure your skills section includes both technical and soft skills mentioned in the job description",
            "Use industry-specific terminology that appears in the job posting"
        )
    }
}