package com.atsoptimizer.aiorchestrator.service

import com.atsoptimizer.aiorchestrator.dto.AnalysisRequest
import com.atsoptimizer.aiorchestrator.dto.AnalysisResponse
import com.atsoptimizer.aiorchestrator.dto.KeywordAnalysis
import com.atsoptimizer.aiorchestrator.dto.ScoreBreakdown
import com.atsoptimizer.aiorchestrator.exception.AnalysisNotFoundException
import com.atsoptimizer.aiorchestrator.model.Analysis
import com.atsoptimizer.aiorchestrator.model.AnalysisStatus
import com.atsoptimizer.aiorchestrator.model.AgentResult
import com.atsoptimizer.aiorchestrator.repository.AnalysisRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}
private val objectMapper = jacksonObjectMapper()

@Service
class AnalysisService(
    private val analysisRepository: AnalysisRepository,
    private val orchestratorService: OrchestratorService
) {

    /**
     * Create and run a new analysis
     */
    @Transactional
    fun createAnalysis(request: AnalysisRequest): AnalysisResponse {
        logger.info { "Creating analysis for user=${request.userId}, doc=${request.documentId}, job=${request.jobId}" }

        // Create initial analysis record
// Create initial analysis record
        val analysis = Analysis(
            documentId = request.documentId,
            jobId = request.jobId,
            userId = request.userId,
            atsScore = 0,
            keywordMatches = null,
            missingKeywords = null,
            suggestions = null,
            atsIssues = null,
            strengths = null,
            weaknesses = null,
            keywordMatchScore = 0,
            atsFormatScore = 0,
            contentQualityScore = 0,
            llmAnalysisScore = 0,
            status = AnalysisStatus.PENDING,
            errorMessage = null,
            createdAt = LocalDateTime.now(),
            completedAt = null
        )

        val savedAnalysis = analysisRepository.save(analysis)
        logger.info { "Analysis created with id: ${savedAnalysis.id}" }

        // Run analysis asynchronously (for now, synchronously with runBlocking)
        // In production, you'd want to use a message queue or Spring @Async
        return runBlocking {
            try {
                processAnalysis(savedAnalysis.id!!)
            } catch (e: Exception) {
                logger.error(e) { "Error processing analysis: ${savedAnalysis.id}" }

                // Update status to failed
                val failedAnalysis = savedAnalysis.copy(
                    status = AnalysisStatus.FAILED,
                    errorMessage = e.message ?: "Unknown error"
                )
                analysisRepository.save(failedAnalysis)

                throw e
            }
        }
    }

    /**
     * Process the analysis using orchestrator
     */
    private suspend fun processAnalysis(analysisId: Long): AnalysisResponse {
        logger.info { "Processing analysis: $analysisId" }

        val analysis = analysisRepository.findById(analysisId)
            .orElseThrow { AnalysisNotFoundException("Analysis not found: $analysisId") }

        // Update status to in progress
        analysisRepository.save(analysis.copy(status = AnalysisStatus.IN_PROGRESS))

        // Run orchestrator
        val results = orchestratorService.orchestrateAnalysis(
            documentId = analysis.documentId,
            jobId = analysis.jobId
        )

        // Extract results
        val keywordResult = results["keywordMatch"] as AgentResult
        val atsResult = results["atsCheck"] as AgentResult
        val suggestionResult = results["suggestion"] as AgentResult
        val finalResult = results["final"] as AgentResult

        // Extract keyword analysis
        val matchedKeywords = extractMatchedKeywords(keywordResult)
        val missingKeywords = extractMissingKeywords(keywordResult)

        // Combine all suggestions
        val allSuggestions = (keywordResult.suggestions +
                atsResult.suggestions +
                suggestionResult.suggestions).distinct()

        val allIssues = atsResult.findings.filter {
            it.contains("missing", ignoreCase = true) ||
                    it.contains("no ", ignoreCase = true) ||
                    it.contains("limited", ignoreCase = true)
        }

        val strengths = (keywordResult.findings + atsResult.findings + suggestionResult.findings)
            .filter {
                it.contains("good", ignoreCase = true) ||
                        it.contains("strong", ignoreCase = true) ||
                        it.contains("appropriate", ignoreCase = true)
            }
            .distinct()
            .take(5)

        val weaknesses = (keywordResult.findings + atsResult.findings)
            .filter {
                it.contains("missing", ignoreCase = true) ||
                        it.contains("lacks", ignoreCase = true) ||
                        it.contains("limited", ignoreCase = true)
            }
            .distinct()
            .take(5)

        // Update analysis with results
        val completedAnalysis = analysis.copy(
            atsScore = finalResult.score,
            keywordMatchScore = keywordResult.score,
            atsFormatScore = atsResult.score,
            contentQualityScore = 75, // Placeholder
            llmAnalysisScore = suggestionResult.score,
            keywordMatches = toJson(matchedKeywords),
            missingKeywords = toJson(missingKeywords),
            suggestions = toJson(allSuggestions),
            atsIssues = toJson(allIssues),
            strengths = toJson(strengths),
            weaknesses = toJson(weaknesses),
            status = AnalysisStatus.COMPLETED,
            completedAt = LocalDateTime.now()
        )

        val saved = analysisRepository.save(completedAnalysis)
        logger.info { "Analysis completed: $analysisId, score: ${saved.atsScore}" }

        return toAnalysisResponse(saved)
    }

    /**
     * Get analysis by ID
     */
    fun getAnalysis(analysisId: Long): AnalysisResponse {
        logger.debug { "Fetching analysis: $analysisId" }

        val analysis = analysisRepository.findById(analysisId)
            .orElseThrow { AnalysisNotFoundException("Analysis not found: $analysisId") }

        return toAnalysisResponse(analysis)
    }

    /**
     * Get all analyses for a user
     */
    fun getUserAnalyses(userId: String): List<AnalysisResponse> {
        logger.debug { "Fetching analyses for user: $userId" }

        return analysisRepository.findByUserId(userId)
            .map { toAnalysisResponse(it) }
    }

    /**
     * Get analyses by document ID
     */
    fun getDocumentAnalyses(documentId: Long): List<AnalysisResponse> {
        logger.debug { "Fetching analyses for document: $documentId" }

        return analysisRepository.findByDocumentId(documentId)
            .map { toAnalysisResponse(it) }
    }

    /**
     * Get analyses by job ID
     */
    fun getJobAnalyses(jobId: Long): List<AnalysisResponse> {
        logger.debug { "Fetching analyses for job: $jobId" }

        return analysisRepository.findByJobId(jobId)
            .map { toAnalysisResponse(it) }
    }

    /**
     * Delete analysis
     */
    @Transactional
    fun deleteAnalysis(analysisId: Long) {
        logger.info { "Deleting analysis: $analysisId" }

        val analysis = analysisRepository.findById(analysisId)
            .orElseThrow { AnalysisNotFoundException("Analysis not found: $analysisId") }

        analysisRepository.delete(analysis)
    }

    /**
     * Convert Analysis entity to AnalysisResponse DTO
     */
    private fun toAnalysisResponse(analysis: Analysis): AnalysisResponse {
        val matchedKeywords = fromJson(analysis.keywordMatches)
        val missingKeywords = fromJson(analysis.missingKeywords)

        val matchPercentage = if ((matchedKeywords.size + missingKeywords.size) > 0) {
            (matchedKeywords.size.toDouble() / (matchedKeywords.size + missingKeywords.size) * 100)
        } else {
            0.0
        }

        return AnalysisResponse(
            id = analysis.id!!,
            documentId = analysis.documentId,
            jobId = analysis.jobId,
            userId = analysis.userId,
            atsScore = analysis.atsScore,
            breakdown = ScoreBreakdown(
                keywordMatch = analysis.keywordMatchScore,
                atsFormat = analysis.atsFormatScore,
                contentQuality = analysis.contentQualityScore,
                llmAnalysis = analysis.llmAnalysisScore,
                overall = analysis.atsScore
            ),
            keywordAnalysis = KeywordAnalysis(
                matched = matchedKeywords,
                missing = missingKeywords,
                matchPercentage = matchPercentage
            ),
            atsIssues = fromJson(analysis.atsIssues),
            suggestions = fromJson(analysis.suggestions),
            strengths = fromJson(analysis.strengths),
            weaknesses = fromJson(analysis.weaknesses),
            status = analysis.status,
            createdAt = analysis.createdAt,
            completedAt = analysis.completedAt
        )
    }

    private fun extractMatchedKeywords(result: AgentResult): List<String> {
        return result.findings
            .firstOrNull { it.contains("Strong matches:") }
            ?.substringAfter("Strong matches:")
            ?.split(",")
            ?.map { it.trim() }
            ?: emptyList()
    }

    private fun extractMissingKeywords(result: AgentResult): List<String> {
        return result.findings
            .firstOrNull { it.contains("Missing keywords:") }
            ?.substringAfter("Missing keywords:")
            ?.split(",")
            ?.map { it.trim() }
            ?: emptyList()
    }

    private fun toJson(list: List<String>): String {
        return try {
            objectMapper.writeValueAsString(list)
        } catch (e: Exception) {
            logger.error(e) { "Error converting list to JSON" }
            "[]"
        }
    }

    private fun fromJson(json: String?): List<String> {
        if (json.isNullOrEmpty()) return emptyList()
        return try {
            objectMapper.readValue(
                json,
                objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)
            )
        } catch (e: Exception) {
            logger.error(e) { "Error parsing JSON: $json" }
            emptyList()
        }
    }
}