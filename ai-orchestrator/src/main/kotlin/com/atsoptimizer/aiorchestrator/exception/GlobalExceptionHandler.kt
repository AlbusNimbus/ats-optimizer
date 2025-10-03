package com.atsoptimizer.aiorchestrator.exception

import com.atsoptimizer.aiorchestrator.dto.ErrorResponse
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

private val logger = KotlinLogging.logger {}

class AnalysisNotFoundException(message: String) : RuntimeException(message)
class ServiceCommunicationException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AnalysisNotFoundException::class)
    fun handleAnalysisNotFound(
        ex: AnalysisNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn { "Analysis not found: ${ex.message}" }
        val error = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Not Found",
            message = ex.message ?: "Analysis not found",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    @ExceptionHandler(ServiceCommunicationException::class)
    fun handleServiceCommunication(
        ex: ServiceCommunicationException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "Service communication error: ${ex.message}" }
        val error = ErrorResponse(
            status = HttpStatus.SERVICE_UNAVAILABLE.value(),
            error = "Service Unavailable",
            message = ex.message ?: "Failed to communicate with external service",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error)
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