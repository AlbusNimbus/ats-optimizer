package com.atsoptimizer.documentprocessor.exception

class DocumentProcessingException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)

class DocumentNotFoundException(message: String) : RuntimeException(message)

class UnsupportedFileTypeException(message: String) : RuntimeException(message)

class FileUploadException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)