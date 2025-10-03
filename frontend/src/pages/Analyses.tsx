import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../store/useStore';
import { analysisService } from '../services/analysisService';
import Card from '../components/common/Card';
import Badge from '../components/common/Badge';
import Spinner from '../components/common/Spinner';
import { formatDateTime } from '../utils/formatters';
import { BarChart3, Eye } from 'lucide-react';

export default function Analyses() {
  const navigate = useNavigate();
  const { userId, analyses, setAnalyses } = useStore();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAnalyses();
  }, []);

  const loadAnalyses = async () => {
    try {
      const analysesList = await analysisService.getUserAnalyses(userId);
      setAnalyses(analysesList);
    } catch (error) {
      console.error('Error loading analyses:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Analysis History</h1>
        <p className="text-gray-600 mt-2">
          View all your resume analyses
        </p>
      </div>

      <Card title={`Your Analyses (${analyses.length})`}>
        {loading ? (
          <div className="flex justify-center py-8">
            <Spinner />
          </div>
        ) : analyses.length === 0 ? (
          <div className="text-center py-12">
            <BarChart3 className="w-12 h-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-500 mb-4">No analyses yet</p>
            <button
              onClick={() => navigate('/analyze')}
              className="btn-primary"
            >
              Run Your First Analysis
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {analyses.map((analysis) => (
              <div
                key={analysis.id}
                className="p-6 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors"
                onClick={() => navigate(`/analyses/${analysis.id}`)}
              >
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center space-x-4">
                    <div className={`w-16 h-16 rounded-lg flex items-center justify-center ${
                      analysis.atsScore >= 80 ? 'bg-green-100' : 
                      analysis.atsScore >= 60 ? 'bg-yellow-100' : 'bg-red-100'
                    }`}>
                      <span className={`text-2xl font-bold ${
                        analysis.atsScore >= 80 ? 'text-green-600' : 
                        analysis.atsScore >= 60 ? 'text-yellow-600' : 'text-red-600'
                      }`}>
                        {analysis.atsScore}
                      </span>
                    </div>
                    <div>
                      <p className="font-semibold text-gray-900">
                        Document #{analysis.documentId} vs Job #{analysis.jobId}
                      </p>
                      <p className="text-sm text-gray-500">
                        {formatDateTime(analysis.createdAt)}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center space-x-3">
                    <Badge variant={analysis.status === 'COMPLETED' ? 'success' : 'warning'}>
                      {analysis.status}
                    </Badge>
                    <Eye className="w-5 h-5 text-gray-400" />
                  </div>
                </div>

                <div className="grid grid-cols-4 gap-4">
                  <div className="text-center p-2 bg-gray-50 rounded">
                    <div className="text-lg font-semibold text-primary-600">
                      {analysis.breakdown.keywordMatch}
                    </div>
                    <div className="text-xs text-gray-600">Keywords</div>
                  </div>
                  <div className="text-center p-2 bg-gray-50 rounded">
                    <div className="text-lg font-semibold text-primary-600">
                      {analysis.breakdown.atsFormat}
                    </div>
                    <div className="text-xs text-gray-600">Format</div>
                  </div>
                  <div className="text-center p-2 bg-gray-50 rounded">
                    <div className="text-lg font-semibold text-primary-600">
                      {analysis.breakdown.contentQuality}
                    </div>
                    <div className="text-xs text-gray-600">Content</div>
                  </div>
                  <div className="text-center p-2 bg-gray-50 rounded">
                    <div className="text-lg font-semibold text-primary-600">
                      {analysis.breakdown.llmAnalysis}
                    </div>
                    <div className="text-xs text-gray-600">AI Score</div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </Card>
    </div>
  );
}
