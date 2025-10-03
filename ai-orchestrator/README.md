# AI Orchestrator Service

Microservice that coordinates multiple AI agents to analyze resumes against job descriptions.

## Features

- Multi-agent analysis system
- Keyword matching agent
- ATS format checker agent
- LLM-powered suggestion agent
- Score aggregation and calculation
- Integration with Claude/OpenAI APIs
- Comprehensive resume scoring (0-100)

## AI Agents

### 1. Keyword Matcher Agent
- Compares resume keywords with job requirements
- Identifies matched and missing keywords
- Calculates keyword match percentage
- Weight: 30% of total score

### 2. ATS Checker Agent
- Validates resume format and structure
- Checks for standard sections (Experience, Education, Skills)
- Verifies contact information presence
- Evaluates use of action verbs and quantifiable achievements
- Weight: 25% of total score

### 3. Suggestion Agent (LLM-Powered)
- Uses Claude/OpenAI for intelligent analysis
- Generates personalized improvement suggestions
- Identifies strengths and weaknesses
- Provides actionable recommendations
- Weight: 20% of total score

### 4. Score Calculator Agent
- Aggregates scores from all agents
- Calculates weighted final ATS score
- Provides score breakdown
- Identifies areas needing improvement
- Weight: Aggregates all other scores

## API Endpoints

### Create Analysis
```http
POST /api/v1/analysis
Content-Type: application/json

{
  "userId": "user-123",
  "documentId": 1,
  "jobId": 1
}

Response:
{
  "id": 1,
  "atsScore": 78,
  "breakdown": {
    "keywordMatch": 75,
    "atsFormat": 82,
    "contentQuality": 75,
    "llmAnalysis": 80,
    "overall": 78
  },
  "keywordAnalysis": {
    "matched": ["java", "spring boot", "aws"],
    "missing": ["kubernetes", "docker"],
    "matchPercentage": 60.0
  },
  "suggestions": [...],
  "strengths": [...],
  "weaknesses": [...]
}