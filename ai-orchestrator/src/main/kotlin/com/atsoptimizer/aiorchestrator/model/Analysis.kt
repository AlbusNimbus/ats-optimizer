package com.atsoptimizer.aiorchestrator.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "analyses")
data class Analysis(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val documentId: Long,

    @Column(nullable = false)
    val jobId: Long,

    @Column(nullable = false)
    val userId: String,

    @Column(nullable = false)
    val atsScore: Int = 0,

    @Column(columnDefinition = "TEXT")
    val keywordMatches: String? = null,

    @Column(columnDefinition = "TEXT")
    val missingKeywords: String? = null,

    @Column(columnDefinition = "TEXT")
    val suggestions: String? = null,

    @Column(columnDefinition = "TEXT")
    val atsIssues: String? = null,

    @Column(columnDefinition = "TEXT")
    val strengths: String? = null,

    @Column(columnDefinition = "TEXT")
    val weaknesses: String? = null,

    @Column
    val keywordMatchScore: Int = 0,

    @Column
    val atsFormatScore: Int = 0,

    @Column
    val contentQualityScore: Int = 0,

    @Column
    val llmAnalysisScore: Int = 0,

    @Column(nullable = false)
    val status: AnalysisStatus = AnalysisStatus.PENDING,

    @Column
    val errorMessage: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)  // CHANGED: was nullable = false
    val completedAt: LocalDateTime? = null
)

enum class AnalysisStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}