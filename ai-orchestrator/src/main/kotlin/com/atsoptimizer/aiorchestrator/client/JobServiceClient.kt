package com.atsoptimizer.aiorchestrator.client

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

private val logger = KotlinLogging.logger {}

@Component
class JobServiceClient(
    @Value("\${services.job-analyzer.url}") private val baseUrl: String
) {
    private val webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .build()

    suspend fun getJob(jobId: Long): JobDto {
        logger.info { "Fetching job: $jobId" }
        return webClient.get()
            .uri("/api/v1/jobs/$jobId")
            .retrieve()
            .awaitBody()
    }
}

data class JobDto(
    val id: Long,
    val title: String,
    val company: String?,
    val description: String,
    val requirements: String?,
    val extractedKeywords: List<String>,
    val requiredSkills: List<String>,
    val preferredSkills: List<String>
)