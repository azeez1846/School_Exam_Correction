/* ==========================================================================
   GRADEPULSE AI - REST API CLIENT & AUTHENTICATION WRAPPER
   ========================================================================== */

const API_BASE_URL = '/api';

class ApiClient {
    constructor() {
        this.token = localStorage.getItem('gradePulseToken') || null;
    }

    setToken(token) {
        this.token = token;
        if (token) {
            localStorage.setItem('gradePulseToken', token);
        } else {
            localStorage.removeItem('gradePulseToken');
        }
    }

    getToken() {
        return this.token;
    }

    getHeaders(isMultipart = false) {
        const headers = {};
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        if (!isMultipart) {
            headers['Content-Type'] = 'application/json';
        }
        return headers;
    }

    async request(endpoint, options = {}) {
        const url = `${API_BASE_URL}${endpoint}`;
        const isMultipart = options.body instanceof FormData;
        
        const config = {
            ...options,
            headers: {
                ...this.getHeaders(isMultipart),
                ...(options.headers || {})
            }
        };

        try {
            const response = await fetch(url, config);
            
            if (response.status === 401) {
                this.setToken(null);
                window.location.reload();
                throw new Error("Session expired. Please log in again.");
            }

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error(`API Error [${endpoint}]:`, error);
            throw error;
        }
    }

    // Auth Endpoints
    async login(username, password) {
        const res = await this.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
        if (res.token) this.setToken(res.token);
        return res;
    }

    async register(username, password, fullName, role) {
        const res = await this.request('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ username, password, fullName, role })
        });
        if (res.token) this.setToken(res.token);
        return res;
    }

    // Exam Endpoints
    async getExams() {
        return await this.request('/exams');
    }

    async createExam(examData) {
        return await this.request('/exams', {
            method: 'POST',
            body: JSON.stringify(examData)
        });
    }

    async getExamRubrics(examId) {
        return await this.request(`/exams/${examId}/rubrics`);
    }

    // Submission & OCR Endpoints
    async submitPaperSingle(formData) {
        return await this.request('/submissions/upload', {
            method: 'POST',
            body: formData
        });
    }

    async submitPaperBulk(formData) {
        return await this.request('/submissions/bulk-upload', {
            method: 'POST',
            body: formData
        });
    }

    async getAllSubmissions() {
        return await this.request('/submissions');
    }

    // Evaluation Endpoints
    async processEvaluation(submissionId, providerKey, modelName, ocrTextOverride) {
        return await this.request('/evaluations/process', {
            method: 'POST',
            body: JSON.stringify({ submissionId, providerKey, modelName, ocrTextOverride })
        });
    }

    async getEvaluationResult(submissionId) {
        return await this.request(`/evaluations/submission/${submissionId}`);
    }

    // Analytics & Settings
    async getDashboardStats() {
        return await this.request('/analytics/dashboard');
    }

    async getLlmConfigs() {
        return await this.request('/settings/llm');
    }

    async saveLlmConfig(config) {
        return await this.request('/settings/llm', {
            method: 'POST',
            body: JSON.stringify(config)
        });
    }
}

const api = new ApiClient();
