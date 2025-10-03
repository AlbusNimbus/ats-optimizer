package com.atsoptimizer.aiorchestrator.service.agent

import com.atsoptimizer.aiorchestrator.model.AgentResult
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

@Component
class ScoreCalculatorAgent(
    @Value("\${agents.keyword-matcher.weight}") private val keywordWeight: Double,
    @Value("\${agents.ats-checker.weight}") private val atsWeight: Double,
    @Value("\${agents.content-quality.weight}") private val contentWeight: Double,
    @Value("\${agents.llm-analysis.weight}") private val llmWeight: Double
) {

    /**
     * Aggregates scores from all agents and calculates final ATS score
     */
    fun calculate(
        keywordScore: Int,
        atsScore: Int,
        contentScore: Int,
        llmScore: Int
    ): AgentResult {
        logger.info { "ScoreCalculatorAgent: Calculating final score" }

        val executionTime = measureTimeMillis {
            // Implementation here
        }

        // Weighted average calculation
        val finalScore = (
                keywordScore * keywordWeight +
                        atsScore * atsWeight +
                        contentScore * contentWeight +
                        llmScore * llmWeight
                ).toInt()

        // Generate findings based on score
        val findings = mutableListOf<String>()
        val suggestions = mutableListOf<String>()

        val rating = when {
            finalScore >= 90 -> "Excellent"
            finalScore >= 80 -> "Very Good"
            finalScore >= 70 -> "Good"
            finalScore >= 60 -> "Fair"
            else -> "Needs Improvement"
        }

        findings.add("Overall ATS Compatibility: $rating ($finalScore/100)")
        findings.add("Keyword Match: $keywordScore/100 (${(keywordWeight * 100).toInt()}% weight)")
        findings.add("ATS Format: $atsScore/100 (${(atsWeight * 100).toInt()}% weight)")
        findings.add("Content Quality: $contentScore/100 (${(contentWeight * 100).toInt()}% weight)")
        findings.add("AI Analysis: $llmScore/100 (${(llmWeight * 100).toInt()}% weight)")

        // Provide score-based guidance
        when {
            finalScore < 60 -> {
                suggestions.add("Your resume needs significant improvements to pass ATS screening")
                suggestions.add("Focus on adding missing keywords and improving formatting")
                suggestions.add("Consider a professional resume review")
            }
            finalScore < 75 -> {
                suggestions.add("Your resume has a moderate chance of passing ATS screening")
                suggestions.add("Review the specific suggestions from each category")
                suggestions.add("Pay special attention to areas with scores below 70")
            }
            finalScore < 85 -> {
                suggestions.add("Your resume has a good chance of passing ATS screening")
                suggestions.add("Make minor adjustments to improve your score further")
                suggestions.add("Focus on areas with the lowest individual scores")
            }
            else -> {
                suggestions.add("Your resume is well-optimized for ATS screening")
                suggestions.add("Continue to tailor it for each specific job application")
                suggestions.add("Ensure all information is current and accurate")
            }
        }

        logger.info { "ScoreCalculatorAgent: Final score calculated: $finalScore" }

        return AgentResult(
            agentName = "ScoreCalculator",
            score = finalScore,
            findings = findings,
            suggestions = suggestions,
            executionTimeMs = executionTime
        )
    }

    /**
     * Identify which component needs most improvement
     */
    fun identifyWeakestArea(
        keywordScore: Int,
        atsScore: Int,
        contentScore: Int,
        llmScore: Int
    ): String {
        val scores = mapOf(
            "Keyword Matching" to keywordScore,
            "ATS Format" to atsScore,
            "Content Quality" to contentScore,
            "Overall Quality" to llmScore
        )

        return scores.minByOrNull { it.value }?.key ?: "Unknown"
    }
}