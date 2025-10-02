package com.atsoptimizer.documentprocessor.service

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertContains
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class TextExtractionServiceTest {

    private val textExtractionService = TextExtractionService()

    @Test
    fun `should extract sections from resume text`() {
        val sampleText = """
            John Doe
            Software Engineer
            
            EXPERIENCE
            Senior Developer at Tech Corp
            Led team of 5 developers
            
            EDUCATION
            BS Computer Science
            MIT, 2015
            
            SKILLS
            Java, Kotlin, Spring Boot
        """.trimIndent()

        val sections = textExtractionService.extractSections(sampleText)

        assertTrue(sections.containsKey("experience"))
        assertTrue(sections.containsKey("education"))
        assertTrue(sections.containsKey("skills"))
        assertContains(sections["experience"]!!, "Tech Corp")
    }
}