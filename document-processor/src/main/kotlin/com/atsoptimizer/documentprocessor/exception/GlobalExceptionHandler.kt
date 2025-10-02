package com.atsoptimizer.documentprocessor.exception

import com.atsoptimizer.documentprocessor.dto.ErrorResponse
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.multipart.MaxUploadSizeExceededException

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DocumentNotFoundException::class)
    fun handleDocumentNotFound(
        ex: DocumentNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn { "Document not found: ${ex.message}" }
        val error = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Not Found",
            message = ex.message ?: "Document not found",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    @ExceptionHandler(UnsupportedFileTypeException::class)
    fun handleUnsupportedFileType(
        ex: UnsupportedFileTypeException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn { "Unsupported file type: ${ex.message}" }
        val error = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = ex.message ?: "Unsupported file type",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.badRequest().body(error)
    }

    @ExceptionHandler(DocumentProcessingException::class)
    fun handleDocumentProcessing(
        ex: DocumentProcessingException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "Document processing error: ${ex.message}" }
        val error = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Processing Error",
            message = ex.message ?: "Failed to process document",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.internalServerError().body(error)
    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSize(
        ex: MaxUploadSizeExceededException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn { "File too large: ${ex.message}" }
        val error = ErrorResponse(
            status = HttpStatus.PAYLOAD_TOO_LARGE.value(),
            error = "Payload Too Large",
            message = "File size exceeds maximum allowed size (10MB)",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "Unexpected error: ${ex.message}" }
        val error = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = "An unexpected error occurred",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.internalServerError().body(error)
    }
}