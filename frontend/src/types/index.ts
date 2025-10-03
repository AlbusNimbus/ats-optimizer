// Document types
export interface Document {
  id: number;
  userId: string;
  fileName: string;
  fileType: string;
  parsedText?: string;
  extractedSections?: Record<string, string>;
  fileSizeBytes: number;
  status: 'UPLOADED' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  errorMessage?: string;
  createdAt: string;
  updatedAt: string;
}

export interface DocumentUploadResponse {
  id: number;
  fileName: string;
  fileType: string;
  status: string;
  message: string;
  createdAt: string;
}

// Job types
export interface Job {
  id: number;
  userId: string;
  title: string;
  company?: string;
  description: string;
  requirements?: string;
  extractedKeywords: string[];
  requiredSkills: string[];
  preferredSkills: string[];
  experienceLevel?: string;
  educationLevel?: string;
  location?: string;
  jobType?: string;
  sourceUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export interface JobCreateRequest {
  userId: string;
  title: string;
  company?: string;
  description: string;
  requirements?: string;
  location?: string;
  jobType?: string;
  sourceUrl?: string;
}

// Analysis types
export interface Analysis {
  id: number;
  documentId: number;
  jobId: number;
  userId: string;
  atsScore: number;
  breakdown: ScoreBreakdown;
  keywordAnalysis: KeywordAnalysis;
  atsIssues: string[];
  suggestions: string[];
  strengths: string[];
  weaknesses: string[];
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';
  createdAt: string;
  completedAt?: string;
}

export interface ScoreBreakdown {
  keywordMatch: number;
  atsFormat: number;
  contentQuality: number;
  llmAnalysis: number;
  overall: number;
}

export interface KeywordAnalysis {
  matched: string[];
  missing: string[];
  matchPercentage: number;
}

export interface AnalysisRequest {
  userId: string;
  documentId: number;
  jobId: number;
}

// API Response types
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}
