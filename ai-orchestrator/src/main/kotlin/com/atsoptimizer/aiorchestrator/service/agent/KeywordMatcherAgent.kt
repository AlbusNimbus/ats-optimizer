package com.atsoptimizer.aiorchestrator.service.agent

import com.atsoptimizer.aiorchestrator.model.AgentResult
import mu.KotlinLogging
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

@Component
class KeywordMatcherAgent {

    /**
     * Analyzes keyword match between resume and job description
     */
    fun analyze(resumeText: String, jobKeywords: List<String>, requiredSkills: List<String>): AgentResult {
        logger.info { "KeywordMatcherAgent: Starting analysis" }

        val executionTime = measureTimeMillis {
            // Implementation here
        }

        val resumeLower = resumeText.lowercase()
        val allKeywords = (jobKeywords + requiredSkills).distinct()

        // Find matched keywords
        val matchedKeywords = allKeywords.filter { keyword ->
            resumeLower.contains(keyword.lowercase())
        }

        // Find missing keywords
        val missingKeywords = allKeywords.filter { keyword ->
            !resumeLower.contains(keyword.lowercase())
        }

        // Calculate score
        val matchPercentage = if (allKeywords.isEmpty()) {
            100
        } else {
            (matchedKeywords.size.toDouble() / allKeywords.size * 100).toInt()
        }

        // Generate findings
        val findings = mutableListOf<String>()
        findings.add("Matched ${matchedKeywords.size} out of ${allKeywords.size} keywords (${matchPercentage}%)")

        if (matchedKeywords.isNotEmpty()) {
            findings.add("Strong matches: ${matchedKeywords.take(5).joinToString(", ")}")
        }

        if (missingKeywords.isNotEmpty()) {
            findings.add("Missing keywords: ${missingKeywords.take(5).joinToString(", ")}")
        }

        // Generate suggestions
        val suggestions = mutableListOf<String>()

        if (matchPercentage < 70) {
            suggestions.add("Your resume is missing ${missingKeywords.size} key terms from the job description")
            suggestions.add("Consider adding these keywords naturally throughout your experience: ${missingKeywords.take(3).joinToString(", ")}")
        }

        if (missingKeywords.any { it in requiredSkills }) {
            val missingRequired = missingKeywords.filter { it in requiredSkills }
            suggestions.add("Critical: You're missing required skills: ${missingRequired.joinToString(", ")}")
        }

        logger.info { "KeywordMatcherAgent: Completed with score $matchPercentage" }

        return AgentResult(
            agentName = "KeywordMatcher",
            score = matchPercentage,
            findings = findings,
            suggestions = suggestions,
            executionTimeMs = executionTime
        )
    }
}