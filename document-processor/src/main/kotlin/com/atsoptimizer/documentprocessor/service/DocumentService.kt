package com.atsoptimizer.documentprocessor.service

import com.atsoptimizer.documentprocessor.dto.DocumentResponse
import com.atsoptimizer.documentprocessor.dto.DocumentUploadResponse
import com.atsoptimizer.documentprocessor.exception.DocumentNotFoundException
import com.atsoptimizer.documentprocessor.exception.UnsupportedFileTypeException
import com.atsoptimizer.documentprocessor.model.Document
import com.atsoptimizer.documentprocessor.model.DocumentStatus
import com.atsoptimizer.documentprocessor.repository.DocumentRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}
private val objectMapper = jacksonObjectMapper()

@Service
class DocumentService(
    private val documentRepository: DocumentRepository,
    private val s3Service: S3Service,
    private val textExtractionService: TextExtractionService
) {

    private val supportedFileTypes = setOf("pdf", "docx", "doc")

    @Transactional
    fun uploadDocument(file: MultipartFile, userId: String): DocumentUploadResponse {
        logger.info { "Processing document upload for user: $userId" }

        val fileType = getFileExtension(file.originalFilename ?: "")
        if (fileType !in supportedFileTypes) {
            throw UnsupportedFileTypeException(
                "File type .$fileType is not supported. Supported types: ${supportedFileTypes.joinToString()}"
            )
        }

        val s3Path = s3Service.uploadFile(file, userId)

        val document = Document(
            userId = userId,
            fileName = file.originalFilename ?: "unnamed",
            fileType = fileType,
            s3Path = s3Path,
            fileSizeBytes = file.size,
            status = DocumentStatus.UPLOADED
        )

        val savedDocument = documentRepository.save(document)

        processDocument(savedDocument.id!!)

        return DocumentUploadResponse(
            id = savedDocument.id!!,
            fileName = savedDocument.fileName,
            fileType = savedDocument.fileType,
            status = savedDocument.status,
            message = "Document uploaded successfully and processing started",
            createdAt = savedDocument.createdAt
        )
    }

    @Transactional
    fun processDocument(documentId: Long) {
        logger.info { "Processing document: $documentId" }

        val document = documentRepository.findById(documentId).orElseThrow {
            DocumentNotFoundException("Document not found: $documentId")
        }

        try {
            documentRepository.save(document.copy(
                status = DocumentStatus.PROCESSING,
                updatedAt = LocalDateTime.now()
            ))

            val inputStream = s3Service.downloadFile(document.s3Path)
            val extractedText = textExtractionService.extractText(inputStream, document.fileType)
            val sections = textExtractionService.extractSections(extractedText)
            val sectionsJson = objectMapper.writeValueAsString(sections)

            val updatedDocument = document.copy(
                parsedText = extractedText,
                extractedSections = sectionsJson,
                status = DocumentStatus.COMPLETED,
                updatedAt = LocalDateTime.now()
            )

            documentRepository.save(updatedDocument)
            logger.info { "Document processed successfully: $documentId" }

        } catch (e: Exception) {
            logger.error(e) { "Failed to process document: $documentId" }
            documentRepository.save(document.copy(
                status = DocumentStatus.FAILED,
                errorMessage = e.message ?: "Unknown error",
                updatedAt = LocalDateTime.now()
            ))
            throw e
        }
    }

    fun getDocument(documentId: Long): DocumentResponse {
        val document = documentRepository.findById(documentId).orElseThrow {
            DocumentNotFoundException("Document not found: $documentId")
        }
        return document.toResponse()
    }

    fun getUserDocuments(userId: String): List<DocumentResponse> {
        return documentRepository.findByUserId(userId)
            .map { it.toResponse() }
    }

    private fun getFileExtension(filename: String): String {
        return filename.substringAfterLast('.', "").lowercase()
    }

    private fun Document.toResponse(): DocumentResponse {
        val sections = if (extractedSections != null) {
            try {
                objectMapper.readValue(extractedSections, Map::class.java) as Map<String, String>
            } catch (e: Exception) {
                null
            }
        } else null

        return DocumentResponse(
            id = id!!,
            userId = userId,
            fileName = fileName,
            fileType = fileType,
            parsedText = parsedText,
            extractedSections = sections,
            fileSizeBytes = fileSizeBytes,
            status = status,
            errorMessage = errorMessage,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}