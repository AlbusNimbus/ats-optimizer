import { Analysis } from '../../types';
import Card from '../common/Card';
import Badge from '../common/Badge';
import ScoreGauge from './ScoreGauge';
import { formatDateTime } from '../../utils/formatters';
import { CheckCircle, XCircle, AlertCircle, TrendingUp } from 'lucide-react';

interface AnalysisResultProps {
  analysis: Analysis;
}

export default function AnalysisResult({ analysis }: AnalysisResultProps) {
  return (
    <div className="space-y-6">
      {/* Overall Score */}
      <Card>
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-bold">Analysis Results</h2>
            <p className="text-sm text-gray-500 mt-1">
              Completed {formatDateTime(analysis.completedAt || analysis.createdAt)}
            </p>
          </div>
          <Badge variant={analysis.status === 'COMPLETED' ? 'success' : 'warning'}>
            {analysis.status}
          </Badge>
        </div>

        <div className="flex justify-center mb-8">
          <ScoreGauge score={analysis.atsScore} label="Overall ATS Score" />
        </div>

        {/* Score Breakdown */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div className="text-center p-4 bg-gray-50 rounded-lg">
            <div className="text-2xl font-bold text-primary-600">
              {analysis.breakdown.keywordMatch}
            </div>
            <div className="text-sm text-gray-600 mt-1">Keyword Match</div>
          </div>
          <div className="text-center p-4 bg-gray-50 rounded-lg">
            <div className="text-2xl font-bold text-primary-600">
              {analysis.breakdown.atsFormat}
            </div>
            <div className="text-sm text-gray-600 mt-1">ATS Format</div>
          </div>
          <div className="text-center p-4 bg-gray-50 rounded-lg">
            <div className="text-2xl font-bold text-primary-600">
              {analysis.breakdown.contentQuality}
            </div>
            <div className="text-sm text-gray-600 mt-1">Content Quality</div>
          </div>
          <div className="text-center p-4 bg-gray-50 rounded-lg">
            <div className="text-2xl font-bold text-primary-600">
              {analysis.breakdown.llmAnalysis}
            </div>
            <div className="text-sm text-gray-600 mt-1">AI Analysis</div>
          </div>
        </div>
      </Card>

      {/* Keyword Analysis */}
      <Card title="Keyword Analysis">
        <div className="grid md:grid-cols-2 gap-6">
          <div>
            <div className="flex items-center mb-3">
              <CheckCircle className="w-5 h-5 text-green-600 mr-2" />
              <h3 className="font-semibold text-gray-900">
                Matched Keywords ({analysis.keywordAnalysis.matched.length})
              </h3>
            </div>
            <div className="flex flex-wrap gap-2">
              {analysis.keywordAnalysis.matched.map((keyword: string, index: number) => (
                <Badge key={index} variant="success">
                  {keyword}
                </Badge>
              ))}
              {analysis.keywordAnalysis.matched.length === 0 && (
                <p className="text-sm text-gray-500">No matched keywords</p>
              )}
            </div>
          </div>
          
          <div>
            <div className="flex items-center mb-3">
              <XCircle className="w-5 h-5 text-red-600 mr-2" />
              <h3 className="font-semibold text-gray-900">
                Missing Keywords ({analysis.keywordAnalysis.missing.length})
              </h3>
            </div>
            <div className="flex flex-wrap gap-2">
              {analysis.keywordAnalysis.missing.map((keyword: string, index: number) => (
                <Badge key={index} variant="error">
                  {keyword}
                </Badge>
              ))}
              {analysis.keywordAnalysis.missing.length === 0 && (
                <p className="text-sm text-gray-500">No missing keywords</p>
              )}
            </div>
          </div>
        </div>

        <div className="mt-4 p-4 bg-blue-50 rounded-lg">
          <div className="text-sm font-medium text-blue-900">
            Match Rate: {analysis.keywordAnalysis.matchPercentage.toFixed(1)}%
          </div>
          <div className="mt-2 bg-blue-200 rounded-full h-2">
            <div
              className="bg-blue-600 h-2 rounded-full transition-all duration-500"
              style={{ width: `${analysis.keywordAnalysis.matchPercentage}%` }}
            />
          </div>
        </div>
      </Card>

      {/* Suggestions */}
      <Card>
        <div className="flex items-center mb-4">
          <TrendingUp className="w-5 h-5 text-primary-600 mr-2" />
          <h3 className="text-xl font-semibold">AI-Powered Suggestions</h3>
        </div>
        <div className="space-y-3">
          {analysis.suggestions.map((suggestion: string, index: number) => (
            <div key={index} className="flex items-start p-3 bg-primary-50 rounded-lg border border-primary-200">
              <div className="flex-shrink-0 w-6 h-6 bg-primary-600 text-white rounded-full flex items-center justify-center text-sm font-medium mr-3">
                {index + 1}
              </div>
              <p className="text-gray-700">{suggestion}</p>
            </div>
          ))}
        </div>
      </Card>

      {/* Strengths & Weaknesses */}
      <div className="grid md:grid-cols-2 gap-6">
        <Card>
          <div className="flex items-center mb-4">
            <CheckCircle className="w-5 h-5 text-green-600 mr-2" />
            <h3 className="text-xl font-semibold">Strengths</h3>
          </div>
          <ul className="space-y-2">
            {analysis.strengths.map((strength: string, index: number) => (
              <li key={index} className="flex items-start">
                <span className="text-green-600 mr-2">✓</span>
                <span className="text-gray-700">{strength}</span>
              </li>
            ))}
          </ul>
        </Card>

        <Card>
          <div className="flex items-center mb-4">
            <AlertCircle className="w-5 h-5 text-yellow-600 mr-2" />
            <h3 className="text-xl font-semibold">Areas to Improve</h3>
          </div>
          <ul className="space-y-2">
            {analysis.weaknesses.map((weakness: string, index: number) => (
              <li key={index} className="flex items-start">
                <span className="text-yellow-600 mr-2">!</span>
                <span className="text-gray-700">{weakness}</span>
              </li>
            ))}
          </ul>
        </Card>
      </div>

      {/* ATS Issues */}
      {analysis.atsIssues.length > 0 && (
        <Card>
          <div className="flex items-center mb-4">
            <AlertCircle className="w-5 h-5 text-red-600 mr-2" />
            <h3 className="text-xl font-semibold">ATS Compatibility Issues</h3>
          </div>
          <ul className="space-y-2">
            {analysis.atsIssues.map((issue: string, index: number) => (
              <li key={index} className="flex items-start p-3 bg-red-50 rounded-lg border border-red-200">
                <span className="text-red-600 mr-2">⚠</span>
                <span className="text-gray-700">{issue}</span>
              </li>
            ))}
          </ul>
        </Card>
      )}
    </div>
  );
}
