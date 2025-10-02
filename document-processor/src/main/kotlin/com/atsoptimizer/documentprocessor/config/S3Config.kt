package com.atsoptimizer.documentprocessor.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class S3Config {

    @Value("\${aws.s3.region}")
    private lateinit var region: String

    @Value("\${aws.credentials.access-key}")
    private lateinit var accessKey: String

    @Value("\${aws.credentials.secret-key}")
    private lateinit var secretKey: String

    @Bean
    fun s3Client(): S3Client {
        return if (accessKey.isNotEmpty() && secretKey.isNotEmpty()) {
            val credentials = AwsBasicCredentials.create(accessKey, secretKey)
            S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build()
        } else {
            S3Client.builder()
                .region(Region.of(region))
                .build()
        }
    }
}