import axios from 'axios';
import { getUserId } from './userService';

const DOCUMENT_SERVICE_URL = import.meta.env.VITE_DOCUMENT_SERVICE_URL || 'http://localhost:8081';
const JOB_SERVICE_URL = import.meta.env.VITE_JOB_SERVICE_URL || 'http://localhost:8082';
const AI_SERVICE_URL = import.meta.env.VITE_AI_SERVICE_URL || 'http://localhost:8083';

export const documentApi = axios.create({
    baseURL: `${DOCUMENT_SERVICE_URL}/api/v1/documents`,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const jobApi = axios.create({
    baseURL: `${JOB_SERVICE_URL}/api/v1/jobs`,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const analysisApi = axios.create({
    baseURL: `${AI_SERVICE_URL}/api/v1/analysis`,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add user ID to all requests
[documentApi, jobApi, analysisApi].forEach(api => {
    api.interceptors.request.use(
        (config) => {
            const userId = getUserId();
            // Add userId to headers
            config.headers['X-User-Id'] = userId;
            return config;
        },
        (error) => Promise.reject(error)
    );

    api.interceptors.response.use(
        (response) => response,
        (error) => {
            console.error('API Error:', error.response?.data || error.message);
            return Promise.reject(error);
        }
    );
});