import { jobApi } from './api';
import { Job, JobCreateRequest } from '../types';

export const jobService = {
  createJob: async (job: JobCreateRequest): Promise<Job> => {
    const response = await jobApi.post<Job>('', job);
    return response.data;
  },

  getJob: async (jobId: number): Promise<Job> => {
    const response = await jobApi.get<Job>(`/${jobId}`);
    return response.data;
  },

  getUserJobs: async (userId: string): Promise<Job[]> => {
    const response = await jobApi.get<Job[]>(`/user/${userId}`);
    return response.data;
  },

  searchJobs: async (keyword: string): Promise<Job[]> => {
    const response = await jobApi.get<Job[]>('/search', {
      params: { keyword },
    });
    return response.data;
  },

  updateJob: async (jobId: number, job: JobCreateRequest): Promise<Job> => {
    const response = await jobApi.put<Job>(`/${jobId}`, job);
    return response.data;
  },

  deleteJob: async (jobId: number): Promise<void> => {
    await jobApi.delete(`/${jobId}`);
  },
};
