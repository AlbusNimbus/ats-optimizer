package com.atsoptimizer.documentprocessor.dto

import com.atsoptimizer.documentprocessor.model.DocumentStatus
import java.time.LocalDateTime

data class DocumentUploadResponse(
    val id: Long,
    val fileName: String,
    val fileType: String,
    val status: DocumentStatus,
    val message: String,
    val createdAt: LocalDateTime
)