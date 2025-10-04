package com.atsoptimizer.aiorchestrator.service.agent

import com.atsoptimizer.aiorchestrator.model.AgentResult
import mu.KotlinLogging
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

@Component
class AtsCheckerAgent {

    /**
     * Checks ATS compatibility of resume format and structure
     */
    fun analyze(resumeText: String, resumeSections: Map<String, String>?): AgentResult {
        logger.info { "AtsCheckerAgent: Starting analysis" }

        val executionTime = measureTimeMillis {
            // Implementation here
        }

        val findings = mutableListOf<String>()
        val suggestions = mutableListOf<String>()
        var score = 100

        // Check 1: Resume length
        val wordCount = resumeText.split("\\s+".toRegex()).size
        when {
            wordCount < 300 -> {
                score -= 15
                findings.add("Resume is too short ($wordCount words)")
                suggestions.add("Expand your resume to 400-800 words for better impact")
            }
            wordCount > 1000 -> {
                score -= 10
                findings.add("Resume is quite long ($wordCount words)")
                suggestions.add("Consider condensing to 600-800 words for better readability")
            }
            else -> {
                findings.add("Resume length is appropriate ($wordCount words)")
            }
        }

        // Check 2: Section structure
        val expectedSections = listOf("experience", "education", "skills")
        val hasAllSections = expectedSections.all { expected ->
            resumeSections?.keys?.any { it.contains(expected, ignoreCase = true) } == true ||
                    resumeText.lowercase().contains(expected)
        }

        if (!hasAllSections) {
            score -= 20
            val missingSections = expectedSections.filter { expected ->
                resumeSections?.keys?.none { it.contains(expected, ignoreCase = true) } == true &&
                        !resumeText.lowercase().contains(expected)
            }
            findings.add("Missing key sections: ${missingSections.joinToString(", ")}")
            suggestions.add("Add these critical sections: ${missingSections.joinToString(", ")}")
        } else {
            findings.add("Resume has all standard sections")
        }

        // Check 3: Contact information
        val hasEmail = resumeText.contains(Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))
        val hasPhone = resumeText.contains(Regex("\\(?\\d{3}\\)?[-.]?\\d{3}[-.]?\\d{4}"))

        if (!hasEmail) {
            score -= 15
            findings.add("No email address detected")
            suggestions.add("Ensure your email address is clearly visible at the top")
        }

        if (!hasPhone) {
            score -= 10
            findings.add("No phone number detected")
            suggestions.add("Add your phone number in standard format")
        }

        // Check 4: Bullet points and formatting
        val hasBullets = resumeText.contains("•") || resumeText.contains("*") ||
                resumeText.contains("-") || resumeText.contains("–")

        if (!hasBullets) {
            score -= 15
            findings.add("No bullet points detected")
            suggestions.add("Use bullet points to highlight achievements and responsibilities")
        } else {
            findings.add("Good use of bullet points for readability")
        }

        // Check 5: Action verbs
        val actionVerbs = listOf(
            "achieved", "improved", "developed", "created", "managed", "led", "designed",
            "implemented", "increased", "reduced", "optimized", "streamlined", "built"
        )
        val usedActionVerbs = actionVerbs.filter {
            resumeText.lowercase().contains(it)
        }

        if (usedActionVerbs.size < 3) {
            score -= 10
            findings.add("Limited use of strong action verbs")
            suggestions.add("Start bullet points with action verbs like: ${actionVerbs.take(5).joinToString(", ")}")
        } else {
            findings.add("Good use of action verbs: ${usedActionVerbs.take(3).joinToString(", ")}")
        }

        // Check 6: Quantifiable achievements
        val hasNumbers = resumeText.contains(Regex("\\d+%")) ||
                resumeText.contains(Regex("\\$\\d+")) ||
                resumeText.contains(Regex("\\d+\\+"))

        if (!hasNumbers) {
            score -= 15
            findings.add("Lacks quantifiable achievements")
            suggestions.add("Add numbers and metrics to demonstrate impact (e.g., 'Increased sales by 30%')")
        } else {
            findings.add("Contains quantifiable achievements")
        }

        // Ensure score doesn't go below 0
        score = maxOf(0, score)

        logger.info { "AtsCheckerAgent: Completed with score $score" }

        return AgentResult(
            agentName = "AtsChecker",
            score = score,
            findings = findings,
            suggestions = suggestions,
            executionTimeMs = executionTime
        )
    }
}