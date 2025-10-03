import { documentApi } from './api';
import { Document, DocumentUploadResponse } from '../types';

export const documentService = {
  uploadDocument: async (file: File, userId: string): Promise<DocumentUploadResponse> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', userId);

    const response = await documentApi.post<DocumentUploadResponse>('/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  getDocument: async (documentId: number): Promise<Document> => {
    const response = await documentApi.get<Document>(`/${documentId}`);
    return response.data;
  },

  getUserDocuments: async (userId: string): Promise<Document[]> => {
    const response = await documentApi.get<Document[]>(`/user/${userId}`);
    return response.data;
  },

  deleteDocument: async (documentId: number): Promise<void> => {
    await documentApi.delete(`/${documentId}`);
  },
};
