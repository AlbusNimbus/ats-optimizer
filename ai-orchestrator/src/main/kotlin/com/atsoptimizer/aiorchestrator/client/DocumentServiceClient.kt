package com.atsoptimizer.aiorchestrator.client

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

private val logger = KotlinLogging.logger {}

@Component
class DocumentServiceClient(
    @Value("\${services.document-processor.url}") private val baseUrl: String
) {
    private val webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .build()

    suspend fun getDocument(documentId: Long): DocumentDto {
        logger.info { "Fetching document: $documentId" }
        return webClient.get()
            .uri("/api/v1/documents/$documentId")
            .retrieve()
            .awaitBody()
    }
}

data class DocumentDto(
    val id: Long,
    val userId: String,
    val fileName: String,
    val parsedText: String?,
    val extractedSections: Map<String, String>?
)