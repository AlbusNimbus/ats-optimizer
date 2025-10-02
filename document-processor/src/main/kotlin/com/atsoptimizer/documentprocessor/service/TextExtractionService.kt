package com.atsoptimizer.documentprocessor.service

import com.atsoptimizer.documentprocessor.exception.DocumentProcessingException
import com.atsoptimizer.documentprocessor.exception.UnsupportedFileTypeException
import mu.KotlinLogging
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import org.springframework.stereotype.Service
import java.io.InputStream

private val logger = KotlinLogging.logger {}

@Service
class TextExtractionService {

    fun extractText(inputStream: InputStream, fileType: String): String {
        return try {
            when (fileType.lowercase()) {
                "pdf" -> extractFromPdf(inputStream)
                "docx" -> extractFromDocx(inputStream)
                "doc" -> extractFromDoc(inputStream)
                else -> throw UnsupportedFileTypeException("Unsupported file type: $fileType")
            }
        } catch (e: UnsupportedFileTypeException) {
            throw e
        } catch (e: Exception) {
            logger.error(e) { "Error extracting text from $fileType file" }
            throw DocumentProcessingException("Failed to extract text from document", e)
        }
    }

    private fun extractFromPdf(inputStream: InputStream): String {
        PDDocument.load(inputStream).use { document ->
            val stripper = PDFTextStripper()
            return stripper.getText(document).trim()
        }
    }

    private fun extractFromDocx(inputStream: InputStream): String {
        XWPFDocument(inputStream).use { document ->
            return document.paragraphs
                .joinToString("\n") { it.text }
                .trim()
        }
    }

    private fun extractFromDoc(inputStream: InputStream): String {
        HWPFDocument(inputStream).use { document ->
            val extractor = WordExtractor(document)
            return extractor.text.trim()
        }
    }

    fun extractSections(text: String): Map<String, String> {
        val sections = mutableMapOf<String, String>()

        val commonSections = listOf(
            "summary", "objective", "experience", "education",
            "skills", "certifications", "projects", "achievements"
        )

        val lines = text.lines()
        var currentSection = "other"
        val sectionContent = mutableMapOf<String, MutableList<String>>()

        lines.forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isNotEmpty()) {
                val matchedSection = commonSections.find {
                    trimmed.lowercase().contains(it) && trimmed.length < 50
                }

                if (matchedSection != null) {
                    currentSection = matchedSection
                } else {
                    sectionContent.getOrPut(currentSection) { mutableListOf() }.add(trimmed)
                }
            }
        }

        sectionContent.forEach { (section, content) ->
            sections[section] = content.joinToString("\n")
        }

        return sections
    }
}