# ATS Optimizer - AI-Powered Resume Analysis Platform

A microservices-based platform that analyzes resumes against job descriptions using AI agents.

## Architecture

- **Document Processor Service** (Port 8081) - Handles resume uploads and text extraction
- **Job Analyzer Service** (Port 8082) - Processes job descriptions [Coming Soon]
- **AI Orchestrator Service** (Port 8083) - AI agent coordination [Coming Soon]
- **API Gateway** (Port 8080) - Single entry point [Coming Soon]
- **Frontend** (Port 5173) - React application [Coming Soon]

## Tech Stack

- **Backend**: Kotlin, Java, Spring Boot
- **Database**: PostgreSQL
- **Storage**: AWS S3
- **Container**: Docker, Kubernetes
- **AI**: Claude/OpenAI APIs

## Getting Started

### Prerequisites
- JDK 17+
- Docker & Docker Compose
- AWS Account (for S3)

### Quick Start

1. Clone the repository
2. Set up environment variables:
   ```bash
   cp .env.example .env
   # Edit .env with your AWS credentials
   ```

3. Run with Docker Compose:
   ```bash
   docker-compose up --build
   ```

4. Test the API:
   ```bash
   curl http://localhost:8081/api/v1/documents/health
   ```

## Services

### Document Processor Service

**Endpoints:**
- `POST /api/v1/documents/upload` - Upload resume
- `GET /api/v1/documents/{id}` - Get document details
- `GET /api/v1/documents/user/{userId}` - Get user's documents

**Features:**
- PDF, DOCX, DOC file support
- Text extraction using Apache PDFBox and POI
- S3 storage integration
- Section detection (Experience, Education, Skills, etc.)

## Development

### Local Development

```bash
cd document-processor
./gradlew bootRun
```

### Running Tests

```bash
./gradlew test
```

### Building Docker Image

```bash
docker build -t document-processor:latest ./document-processor
```

## Project Structure

See [document-processor/README.md](document-processor/README.md) for detailed structure.

## License

MIT
