package com.atsoptimizer.aiorchestrator.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class AnalysisRequest(
    @field:NotBlank(message = "User ID is required")
    val userId: String,

    @field:NotNull(message = "Document ID is required")
    val documentId: Long,

    @field:NotNull(message = "Job ID is required")
    val jobId: Long
)