package com.atsoptimizer.documentprocessor.service

import com.atsoptimizer.documentprocessor.exception.DocumentProcessingException
import com.atsoptimizer.documentprocessor.exception.FileUploadException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.InputStream
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class S3Service(
    private val s3Client: S3Client,
    @Value("\${aws.s3.bucket-name}") private val bucketName: String
) {

    fun uploadFile(file: MultipartFile, userId: String): String {
        try {
            val fileKey = generateFileKey(userId, file.originalFilename ?: "unnamed")

            val putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(file.contentType)
                .build()

            s3Client.putObject(putRequest, RequestBody.fromBytes(file.bytes))

            logger.info { "File uploaded successfully to S3: $fileKey" }
            return fileKey
        } catch (e: Exception) {
            logger.error(e) { "Failed to upload file to S3" }
            throw FileUploadException("Failed to upload file to S3", e)
        }
    }

    fun downloadFile(s3Path: String): InputStream {
        try {
            val getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Path)
                .build()

            return s3Client.getObject(getRequest)
        } catch (e: Exception) {
            logger.error(e) { "Failed to download file from S3: $s3Path" }
            throw DocumentProcessingException("Failed to download file from S3", e)
        }
    }

    private fun generateFileKey(userId: String, fileName: String): String {
        val timestamp = System.currentTimeMillis()
        val uuid = UUID.randomUUID().toString().substring(0, 8)
        val sanitizedFileName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        return "documents/$userId/$timestamp-$uuid-$sanitizedFileName"
    }
}