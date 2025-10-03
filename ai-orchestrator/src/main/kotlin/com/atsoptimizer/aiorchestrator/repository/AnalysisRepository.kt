package com.atsoptimizer.aiorchestrator.repository

import com.atsoptimizer.aiorchestrator.model.Analysis
import com.atsoptimizer.aiorchestrator.model.AnalysisStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnalysisRepository : JpaRepository<Analysis, Long> {
    fun findByUserId(userId: String): List<Analysis>
    fun findByDocumentId(documentId: Long): List<Analysis>
    fun findByJobId(jobId: Long): List<Analysis>
    fun findByUserIdAndStatus(userId: String, status: AnalysisStatus): List<Analysis>
}