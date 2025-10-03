# Job Analyzer Service

Microservice for analyzing job descriptions and extracting requirements.

## Features

- Job posting creation and management
- Automatic keyword extraction
- Required vs preferred skills identification
- Experience and education level detection
- Redis caching for performance
- Full-text search capabilities

## API Endpoints

### Create Job
```http
POST /api/v1/jobs
Content-Type: application/json

{
  "userId": "user-123",
  "title": "Senior Software Engineer",
  "company": "Tech Corp",
  "description": "We are looking for...",
  "requirements": "5+ years of Java experience...",
  "location": "San Francisco, CA",
  "jobType": "Full-time"
}