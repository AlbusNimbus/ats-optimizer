import { getScoreColor, getScoreLabel } from '../../utils/formatters';

interface ScoreGaugeProps {
  score: number;
  label?: string;
}

export default function ScoreGauge({ score, label }: ScoreGaugeProps) {
  const circumference = 2 * Math.PI * 45;
  const strokeDashoffset = circumference - (score / 100) * circumference;

  return (
    <div className="flex flex-col items-center">
      <div className="relative w-32 h-32">
        <svg className="transform -rotate-90 w-32 h-32">
          <circle
            cx="64"
            cy="64"
            r="45"
            stroke="currentColor"
            strokeWidth="10"
            fill="transparent"
            className="text-gray-200"
          />
          <circle
            cx="64"
            cy="64"
            r="45"
            stroke="currentColor"
            strokeWidth="10"
            fill="transparent"
            strokeDasharray={circumference}
            strokeDashoffset={strokeDashoffset}
            className={getScoreColor(score)}
            strokeLinecap="round"
          />
        </svg>
        <div className="absolute inset-0 flex items-center justify-center">
          <div className="text-center">
            <div className={`text-3xl font-bold ${getScoreColor(score)}`}>
              {score}
            </div>
            <div className="text-xs text-gray-500">/ 100</div>
          </div>
        </div>
      </div>
      {label && (
        <div className="mt-2 text-sm font-medium text-gray-600">{label}</div>
      )}
      <div className={`mt-1 text-xs font-semibold ${getScoreColor(score)}`}>
        {getScoreLabel(score)}
      </div>
    </div>
  );
}
