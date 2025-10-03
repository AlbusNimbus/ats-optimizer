package com.atsoptimizer.aiorchestrator.controller

import com.atsoptimizer.aiorchestrator.dto.AnalysisRequest
import com.atsoptimizer.aiorchestrator.dto.AnalysisResponse
import com.atsoptimizer.aiorchestrator.service.AnalysisService
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/analysis")
class AnalysisController(
    private val analysisService: AnalysisService
) {

    /**
     * Create a new analysis
     */
    @PostMapping
    fun createAnalysis(@Valid @RequestBody request: AnalysisRequest): ResponseEntity<AnalysisResponse> {
        logger.info { "Received analysis request: doc=${request.documentId}, job=${request.jobId}" }

        val response = analysisService.createAnalysis(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * Get analysis by ID
     */
    @GetMapping("/{analysisId}")
    fun getAnalysis(@PathVariable analysisId: Long): ResponseEntity<AnalysisResponse> {
        logger.info { "Fetching analysis: $analysisId" }

        val response = analysisService.getAnalysis(analysisId)
        return ResponseEntity.ok(response)
    }

    /**
     * Get all analyses for a user
     */
    @GetMapping("/user/{userId}")
    fun getUserAnalyses(@PathVariable userId: String): ResponseEntity<List<AnalysisResponse>> {
        logger.info { "Fetching analyses for user: $userId" }

        val response = analysisService.getUserAnalyses(userId)
        return ResponseEntity.ok(response)
    }

    /**
     * Get analyses by document ID
     */
    @GetMapping("/document/{documentId}")
    fun getDocumentAnalyses(@PathVariable documentId: Long): ResponseEntity<List<AnalysisResponse>> {
        logger.info { "Fetching analyses for document: $documentId" }

        val response = analysisService.getDocumentAnalyses(documentId)
        return ResponseEntity.ok(response)
    }

    /**
     * Get analyses by job ID
     */
    @GetMapping("/job/{jobId}")
    fun getJobAnalyses(@PathVariable jobId: Long): ResponseEntity<List<AnalysisResponse>> {
        logger.info { "Fetching analyses for job: $jobId" }

        val response = analysisService.getJobAnalyses(jobId)
        return ResponseEntity.ok(response)
    }

    /**
     * Delete analysis
     */
    @DeleteMapping("/{analysisId}")
    fun deleteAnalysis(@PathVariable analysisId: Long): ResponseEntity<Void> {
        logger.info { "Deleting analysis: $analysisId" }

        analysisService.deleteAnalysis(analysisId)
        return ResponseEntity.noContent().build()
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf(
            "status" to "UP",
            "service" to "ai-orchestrator"
        ))
    }
}