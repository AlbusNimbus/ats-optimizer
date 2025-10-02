package com.atsoptimizer.documentprocessor.controller

import com.atsoptimizer.documentprocessor.dto.DocumentResponse
import com.atsoptimizer.documentprocessor.dto.DocumentUploadResponse
import com.atsoptimizer.documentprocessor.service.DocumentService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/documents")
class DocumentController(
    private val documentService: DocumentService
) {

    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadDocument(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("userId") userId: String
    ): ResponseEntity<DocumentUploadResponse> {
        logger.info { "Received upload request from user: $userId, file: ${file.originalFilename}" }

        val response = documentService.uploadDocument(file, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{documentId}")
    fun getDocument(@PathVariable documentId: Long): ResponseEntity<DocumentResponse> {
        logger.info { "Fetching document: $documentId" }

        val response = documentService.getDocument(documentId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/user/{userId}")
    fun getUserDocuments(@PathVariable userId: String): ResponseEntity<List<DocumentResponse>> {
        logger.info { "Fetching documents for user: $userId" }

        val response = documentService.getUserDocuments(userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf(
            "status" to "UP",
            "service" to "document-processor"
        ))
    }
}