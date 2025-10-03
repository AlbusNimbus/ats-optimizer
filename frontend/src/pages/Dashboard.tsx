import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../store/useStore';
import { documentService } from '../services/documentService';
import { jobService } from '../services/jobService';
import { analysisService } from '../services/analysisService';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import Spinner from '../components/common/Spinner';
import { FileText, Briefcase, BarChart3, Plus } from 'lucide-react';

export default function Dashboard() {
  const navigate = useNavigate();
  const { userId, documents, jobs, analyses, setDocuments, setJobs, setAnalyses } = useStore();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [docs, jobsList, analysesList] = await Promise.all([
        documentService.getUserDocuments(userId),
        jobService.getUserJobs(userId),
        analysisService.getUserAnalyses(userId),
      ]);

      setDocuments(docs);
      setJobs(jobsList);
      setAnalyses(analysesList);
    } catch (error) {
      console.error('Error loading data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Spinner size="lg" />
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600 mt-2">
          Welcome back! Analyze your resumes against job descriptions.
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Resumes</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{documents.length}</p>
            </div>
            <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center">
              <FileText className="w-6 h-6 text-primary-600" />
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Job Postings</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{jobs.length}</p>
            </div>
            <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
              <Briefcase className="w-6 h-6 text-green-600" />
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Analyses</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{analyses.length}</p>
            </div>
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
              <BarChart3 className="w-6 h-6 text-blue-600" />
            </div>
          </div>
        </Card>
      </div>

      {/* Quick Actions */}
      <Card title="Quick Actions">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Button onClick={() => navigate('/documents')} className="flex items-center justify-center">
            <Plus className="w-4 h-4 mr-2" />
            Upload Resume
          </Button>
          <Button onClick={() => navigate('/jobs')} variant="secondary" className="flex items-center justify-center">
            <Plus className="w-4 h-4 mr-2" />
            Add Job
          </Button>
          <Button onClick={() => navigate('/analyze')} variant="secondary" className="flex items-center justify-center">
            <BarChart3 className="w-4 h-4 mr-2" />
            Run Analysis
          </Button>
        </div>
      </Card>

      {/* Recent Analyses */}
      {analyses.length > 0 && (
        <Card title="Recent Analyses">
          <div className="space-y-3">
            {analyses.slice(0, 5).map((analysis) => (
              <div
                key={analysis.id}
                onClick={() => navigate(`/analyses/${analysis.id}`)}
                className="flex items-center justify-between p-4 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors"
              >
                <div className="flex items-center space-x-4">
                  <div className={`w-12 h-12 rounded-lg flex items-center justify-center ${
                    analysis.atsScore >= 80 ? 'bg-green-100' : 
                    analysis.atsScore >= 60 ? 'bg-yellow-100' : 'bg-red-100'
                  }`}>
                    <span className={`text-xl font-bold ${
                      analysis.atsScore >= 80 ? 'text-green-600' : 
                      analysis.atsScore >= 60 ? 'text-yellow-600' : 'text-red-600'
                    }`}>
                      {analysis.atsScore}
                    </span>
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">
                      Document #{analysis.documentId} vs Job #{analysis.jobId}
                    </p>
                    <p className="text-sm text-gray-500">
                      {new Date(analysis.createdAt).toLocaleDateString()}
                    </p>
                  </div>
                </div>
                <div className="text-primary-600 hover:text-primary-700">
                  View Details →
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}
    </div>
  );
}
