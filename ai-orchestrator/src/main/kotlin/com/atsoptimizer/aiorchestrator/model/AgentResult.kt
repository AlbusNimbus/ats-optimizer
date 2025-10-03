package com.atsoptimizer.aiorchestrator.model

data class AgentResult(
    val agentName: String,
    val score: Int,  // 0-100
    val findings: List<String>,
    val suggestions: List<String>,
    val executionTimeMs: Long
)