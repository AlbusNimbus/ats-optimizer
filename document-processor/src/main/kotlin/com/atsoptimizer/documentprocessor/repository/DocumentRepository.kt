package com.atsoptimizer.documentprocessor.repository

import com.atsoptimizer.documentprocessor.model.Document
import com.atsoptimizer.documentprocessor.model.DocumentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DocumentRepository : JpaRepository<Document, Long> {
    fun findByUserId(userId: String): List<Document>
    fun findByUserIdAndStatus(userId: String, status: DocumentStatus): List<Document>
}