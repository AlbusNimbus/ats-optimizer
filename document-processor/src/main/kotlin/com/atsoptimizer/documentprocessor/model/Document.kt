package com.atsoptimizer.documentprocessor.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "documents")
data class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: String,

    @Column(nullable = false)
    val fileName: String,

    @Column(nullable = false)
    val fileType: String,

    @Column(nullable = false)
    val s3Path: String,

    @Column(columnDefinition = "TEXT")
    val parsedText: String? = null,

    @Column(columnDefinition = "TEXT")
    val extractedSections: String? = null,

    @Column(nullable = false)
    val fileSizeBytes: Long,

    @Column(nullable = false)
    val status: DocumentStatus = DocumentStatus.UPLOADED,

    @Column
    val errorMessage: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class DocumentStatus {
    UPLOADED,
    PROCESSING,
    COMPLETED,
    FAILED
}