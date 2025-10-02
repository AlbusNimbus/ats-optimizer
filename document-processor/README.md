# Document Processor Service

Microservice responsible for handling resume uploads, text extraction, and storage.

## Features

- Multi-format support (PDF, DOCX, DOC)
- AWS S3 integration for file storage
- Text extraction and section detection
- PostgreSQL persistence
- REST API with proper error handling

## API Endpoints

### Upload Document
```http
POST /api/v1/documents/upload
Content-Type: multipart/form-data

Parameters:
- file: The resume file (PDF, DOCX, DOC)
- userId: User identifier

Response:
{
  "id": 1,
  "fileName": "resume.pdf",
  "fileType": "pdf",
  "status": "COMPLETED",
  "message": "Document uploaded successfully",
  "createdAt": "2025-10-02T10:00:00"
}
```

### Get Document
```http
GET /api/v1/documents/{documentId}

Response:
{
  "id": 1,
  "userId": "user-123",
  "fileName": "resume.pdf",
  "fileType": "pdf",
  "parsedText": "John Doe\nSoftware Engineer...",
  "extractedSections": {
    "experience": "...",
    "education": "...",
    "skills": "..."
  },
  "status": "COMPLETED",
  "createdAt": "2025-10-02T10:00:00"
}
```

### Get User Documents
```http
GET /api/v1/documents/user/{userId}

Response:
[
  { document1 },
  { document2 }
]
```

## Local Development

### Prerequisites
- JDK 17+
- PostgreSQL 15+
- AWS account with S3 access

### Setup

1. Set environment variables:
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=ats_optimizer
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export AWS_S3_BUCKET=your-bucket-name
export AWS_REGION=us-east-1
export AWS_ACCESS_KEY=your-access-key
export AWS_SECRET_KEY=your-secret-key
```

2. Run the application:
```bash
./gradlew bootRun
```

### Testing

Run tests:
```bash
./gradlew test
```

### Build

Build JAR:
```bash
./gradlew build
```

Build Docker image:
```bash
docker build -t document-processor:latest .
```

## Configuration

See `application.yml` for configuration options.

Key configurations:
- File size limit: 10MB
- Supported formats: PDF, DOCX, DOC
- Database: PostgreSQL
- Storage: AWS S3

## Architecture

```
Controller → Service → Repository → Database
              ↓
         S3Service → AWS S3
              ↓
    TextExtractionService
```

## Dependencies

- Spring Boot 3.2.0
- Kotlin 1.9.20
- PostgreSQL JDBC Driver
- AWS SDK for S3
- Apache PDFBox (PDF processing)
- Apache POI (Office documents)
