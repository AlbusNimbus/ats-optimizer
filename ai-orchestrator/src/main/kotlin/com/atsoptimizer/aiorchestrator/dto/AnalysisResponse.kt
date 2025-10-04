package com.atsoptimizer.aiorchestrator.dto

import com.atsoptimizer.aiorchestrator.model.AnalysisStatus
import java.time.LocalDateTime

data class AnalysisResponse(
    val id: Long,
    val documentId: Long,
    val jobId: Long,
    val userId: String,
    val atsScore: Int,
    val breakdown: ScoreBreakdown,
    val keywordAnalysis: KeywordAnalysis,
    val atsIssues: List<String>,
    val suggestions: List<String>,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val status: AnalysisStatus,
    val createdAt: LocalDateTime,
    val completedAt: LocalDateTime?
)

data class ScoreBreakdown(
    val keywordMatch: Int,
    val atsFormat: Int,
    val contentQuality: Int,
    val llmAnalysis: Int,
    val overall: Int
)

data class KeywordAnalysis(
    val matched: List<String>,
    val missing: List<String>,
    val matchPercentage: Double
)