import { create } from 'zustand';
import { Document, Job, Analysis } from '../types';
import { getUserId } from '../services/userService';

interface AppState {
  userId: string;
  setUserId: (userId: string) => void;
  documents: Document[];
  setDocuments: (documents: Document[]) => void;
  addDocument: (document: Document) => void;
  jobs: Job[];
  setJobs: (jobs: Job[]) => void;
  addJob: (job: Job) => void;
  analyses: Analysis[];
  setAnalyses: (analyses: Analysis[]) => void;
  addAnalysis: (analysis: Analysis) => void;
  selectedDocument: Document | null;
  setSelectedDocument: (document: Document | null) => void;
  selectedJob: Job | null;
  setSelectedJob: (job: Job | null) => void;
}

export const useStore = create<AppState>((set) => ({
  userId: getUserId(), // Generate unique ID per browser
  setUserId: (userId) => set({ userId }),
  
  documents: [],
  setDocuments: (documents) => set({ documents }),
  addDocument: (document) => set((state) => ({ 
    documents: [...state.documents, document] 
  })),
  
  jobs: [],
  setJobs: (jobs) => set({ jobs }),
  addJob: (job) => set((state) => ({ 
    jobs: [...state.jobs, job] 
  })),
  
  analyses: [],
  setAnalyses: (analyses) => set({ analyses }),
  addAnalysis: (analysis) => set((state) => ({
    analyses: [...state.analyses, analysis]
  })),
  
  selectedDocument: null,
  setSelectedDocument: (document) => set({ selectedDocument: document }),
  
  selectedJob: null,
  setSelectedJob: (job) => set({ selectedJob: job }),
}));
