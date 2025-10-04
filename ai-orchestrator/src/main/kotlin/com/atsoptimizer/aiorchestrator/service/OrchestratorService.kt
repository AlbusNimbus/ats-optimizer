package com.atsoptimizer.aiorchestrator.service

import com.atsoptimizer.aiorchestrator.client.DocumentServiceClient
import com.atsoptimizer.aiorchestrator.client.JobServiceClient
import com.atsoptimizer.aiorchestrator.model.AgentResult
import com.atsoptimizer.aiorchestrator.service.agent.*
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class OrchestratorService(
    private val documentServiceClient: DocumentServiceClient,
    private val jobServiceClient: JobServiceClient,
    private val keywordMatcherAgent: KeywordMatcherAgent,
    private val atsCheckerAgent: AtsCheckerAgent,
    private val suggestionAgent: SuggestionAgent,
    private val scoreCalculatorAgent: ScoreCalculatorAgent
) {

    /**
     * Orchestrate all agents to perform complete analysis
     */
    suspend fun orchestrateAnalysis(
        documentId: Long,
        jobId: Long
    ): Map<String, Any> = coroutineScope {
        logger.info { "Orchestrator: Starting analysis for document=$documentId, job=$jobId" }

        // Step 1: Fetch document and job in parallel
        val documentDeferred = async { documentServiceClient.getDocument(documentId) }
        val jobDeferred = async { jobServiceClient.getJob(jobId) }

        val document = documentDeferred.await()
        val job = jobDeferred.await()

        logger.info { "Orchestrator: Fetched document and job successfully" }

        // Validate data
        if (document.parsedText.isNullOrEmpty()) {
            throw IllegalStateException("Document has no parsed text")
        }

        // Step 2: Run agents in parallel where possible
        val keywordMatchResult = async {
            keywordMatcherAgent.analyze(
                resumeText = document.parsedText,
                jobKeywords = job.extractedKeywords,
                requiredSkills = job.requiredSkills
            )
        }

        val atsCheckResult = async {
            atsCheckerAgent.analyze(
                resumeText = document.parsedText,
                resumeSections = document.extractedSections
            )
        }

        // Wait for initial results
        val keywordResult = keywordMatchResult.await()
        val atsResult = atsCheckResult.await()

        logger.info { "Orchestrator: Completed keyword and ATS analysis" }

        // Step 3: Run suggestion agent (needs results from previous agents)
        val missingKeywords = extractMissingKeywords(keywordResult)

        val suggestionResult = suggestionAgent.analyze(
            resumeText = document.parsedText,
            jobTitle = job.title,
            jobDescription = job.description,
            missingKeywords = missingKeywords
        )

        logger.info { "Orchestrator: Completed LLM-based suggestion analysis" }

        // Step 4: Calculate final score
        val finalResult = scoreCalculatorAgent.calculate(
            keywordScore = keywordResult.score,
            atsScore = atsResult.score,
            contentScore = 75, // Placeholder for now
            llmScore = suggestionResult.score
        )

        logger.info { "Orchestrator: Analysis complete. Final score: ${finalResult.score}" }

        // Return comprehensive results
        mapOf(
            "keywordMatch" to keywordResult,
            "atsCheck" to atsResult,
            "suggestion" to suggestionResult,
            "final" to finalResult,
            "document" to document,
            "job" to job
        )
    }

    private fun extractMissingKeywords(keywordResult: AgentResult): List<String> {
        return keywordResult.findings
            .firstOrNull { it.contains("Missing keywords:") }
            ?.substringAfter("Missing keywords:")
            ?.split(",")
            ?.map { it.trim() }
            ?: emptyList()
    }
}