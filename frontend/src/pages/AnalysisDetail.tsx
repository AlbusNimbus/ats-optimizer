import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { analysisService } from '../services/analysisService';
import { Analysis } from '../types';
import AnalysisResult from '../components/analysis/AnalysisResult';
import Spinner from '../components/common/Spinner';
import Button from '../components/common/Button';
import { ArrowLeft } from 'lucide-react';

export default function AnalysisDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [analysis, setAnalysis] = useState<Analysis | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAnalysis();
  }, [id]);

  const loadAnalysis = async () => {
    if (!id) return;
    
    try {
      const data = await analysisService.getAnalysis(parseInt(id));
      setAnalysis(data);
    } catch (error) {
      console.error('Error loading analysis:', error);
      alert('Failed to load analysis');
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

  if (!analysis) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-500 mb-4">Analysis not found</p>
        <Button onClick={() => navigate('/analyses')}>
          Back to Analyses
        </Button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <Button
        onClick={() => navigate('/analyses')}
        variant="secondary"
        className="flex items-center"
      >
        <ArrowLeft className="w-4 h-4 mr-2" />
        Back to Analyses
      </Button>

      <AnalysisResult analysis={analysis} />
    </div>
  );
}
