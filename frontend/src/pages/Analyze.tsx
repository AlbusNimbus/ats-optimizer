import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../store/useStore';
import { analysisService } from '../services/analysisService';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import { FileText, Briefcase, PlayCircle } from 'lucide-react';

export default function Analyze() {
  const navigate = useNavigate();
  const { userId, documents, jobs, addAnalysis } = useStore();
  const [selectedDocumentId, setSelectedDocumentId] = useState<number | null>(null);
  const [selectedJobId, setSelectedJobId] = useState<number | null>(null);
  const [analyzing, setAnalyzing] = useState(false);

  const handleAnalyze = async () => {
    if (!selectedDocumentId || !selectedJobId) {
      alert('Please select both a resume and a job posting');
      return;
    }

    setAnalyzing(true);
    try {
      const analysis = await analysisService.createAnalysis({
        userId,
        documentId: selectedDocumentId,
        jobId: selectedJobId,
      });

      addAnalysis(analysis);
      alert('Analysis completed!');
      navigate(`/analyses/${analysis.id}`);
    } catch (error) {
      console.error('Analysis error:', error);
      alert('Failed to run analysis. Please try again.');
    } finally {
      setAnalyzing(false);
    }
  };

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Run Analysis</h1>
        <p className="text-gray-600 mt-2">
          Compare your resume against a job description
        </p>
      </div>

      <div className="grid md:grid-cols-2 gap-6">
        {/* Select Resume */}
        <Card>
          <div className="flex items-center mb-4">
            <FileText className="w-5 h-5 text-primary-600 mr-2" />
            <h3 className="text-xl font-semibold">Select Resume</h3>
          </div>

          {documents.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-gray-500 mb-4">No resumes uploaded yet</p>
              <Button onClick={() => navigate('/documents')}>
                Upload Resume
              </Button>
            </div>
          ) : (
            <div className="space-y-2">
              {documents.map((doc) => (
                <div
                  key={doc.id}
                  onClick={() => setSelectedDocumentId(doc.id)}
                  className={`p-4 border-2 rounded-lg cursor-pointer transition-colors ${
                    selectedDocumentId === doc.id
                      ? 'border-primary-600 bg-primary-50'
                      : 'border-gray-200 hover:border-primary-300'
                  }`}
                >
                  <p className="font-medium text-gray-900">{doc.fileName}</p>
                  <p className="text-sm text-gray-500">{doc.fileType.toUpperCase()}</p>
                </div>
              ))}
            </div>
          )}
        </Card>

        {/* Select Job */}
        <Card>
          <div className="flex items-center mb-4">
            <Briefcase className="w-5 h-5 text-primary-600 mr-2" />
            <h3 className="text-xl font-semibold">Select Job</h3>
          </div>

          {jobs.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-gray-500 mb-4">No jobs added yet</p>
              <Button onClick={() => navigate('/jobs')}>
                Add Job
              </Button>
            </div>
          ) : (
            <div className="space-y-2">
              {jobs.map((job) => (
                <div
                  key={job.id}
                  onClick={() => setSelectedJobId(job.id)}
                  className={`p-4 border-2 rounded-lg cursor-pointer transition-colors ${
                    selectedJobId === job.id
                      ? 'border-primary-600 bg-primary-50'
                      : 'border-gray-200 hover:border-primary-300'
                  }`}
                >
                  <p className="font-medium text-gray-900">{job.title}</p>
                  {job.company && (
                    <p className="text-sm text-gray-500">{job.company}</p>
                  )}
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>

      {/* Run Analysis Button */}
      <Card>
        <div className="text-center">
          <Button
            onClick={handleAnalyze}
            isLoading={analyzing}
            disabled={!selectedDocumentId || !selectedJobId}
            className="px-8 py-3 text-lg"
          >
            <PlayCircle className="w-5 h-5 mr-2 inline" />
            {analyzing ? 'Analyzing...' : 'Run Analysis'}
          </Button>
          {analyzing && (
            <p className="text-sm text-gray-500 mt-4">
              This may take 10-20 seconds...
            </p>
          )}
        </div>
      </Card>
    </div>
  );
}
