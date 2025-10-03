import { analysisApi } from './api';
import { Analysis, AnalysisRequest } from '../types';

export const analysisService = {
  createAnalysis: async (request: AnalysisRequest): Promise<Analysis> => {
    const response = await analysisApi.post<Analysis>('', request);
    return response.data;
  },

  getAnalysis: async (analysisId: number): Promise<Analysis> => {
    const response = await analysisApi.get<Analysis>(`/${analysisId}`);
    return response.data;
  },

  getUserAnalyses: async (userId: string): Promise<Analysis[]> => {
    const response = await analysisApi.get<Analysis[]>(`/user/${userId}`);
    return response.data;
  },

  getDocumentAnalyses: async (documentId: number): Promise<Analysis[]> => {
    const response = await analysisApi.get<Analysis[]>(`/document/${documentId}`);
    return response.data;
  },

  getJobAnalyses: async (jobId: number): Promise<Analysis[]> => {
    const response = await analysisApi.get<Analysis[]>(`/job/${jobId}`);
    return response.data;
  },

  deleteAnalysis: async (analysisId: number): Promise<void> => {
    await analysisApi.delete(`/${analysisId}`);
  },
};
