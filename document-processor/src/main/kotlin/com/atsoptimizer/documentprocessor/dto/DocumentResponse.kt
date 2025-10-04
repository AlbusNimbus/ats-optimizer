package com.atsoptimizer.documentprocessor.dto

import com.atsoptimizer.documentprocessor.model.DocumentStatus
import java.time.LocalDateTime

data class DocumentResponse(
    val id: Long,
    val userId: String,
    val fileName: String,
    val fileType: String,
    val parsedText: String?,
    val extractedSections: Map<String, String>?,
    val fileSizeBytes: Long,
    val status: DocumentStatus,
    val errorMessage: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)