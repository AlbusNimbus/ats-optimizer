import { useEffect, useState } from 'react';
import { useStore } from '../store/useStore';
import { jobService } from '../services/jobService';
import JobForm from '../components/jobs/JobForm';
import Card from '../components/common/Card';
import Badge from '../components/common/Badge';
import Spinner from '../components/common/Spinner';
import { formatDate } from '../utils/formatters';
import { Briefcase, Trash2, MapPin } from 'lucide-react';

export default function Jobs() {
  const { userId, jobs, setJobs } = useStore();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadJobs();
  }, []);

  const loadJobs = async () => {
    try {
      const jobsList = await jobService.getUserJobs(userId);
      setJobs(jobsList);
    } catch (error) {
      console.error('Error loading jobs:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (jobId: number) => {
    if (!confirm('Are you sure you want to delete this job?')) return;

    try {
      await jobService.deleteJob(jobId);
      setJobs(jobs.filter(j => j.id !== jobId));
    } catch (error) {
      console.error('Error deleting job:', error);
      alert('Failed to delete job');
    }
  };

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Job Postings</h1>
        <p className="text-gray-600 mt-2">Add and manage job descriptions</p>
      </div>

      <JobForm />

      <Card title={`Your Jobs (${jobs.length})`}>
        {loading ? (
          <div className="flex justify-center py-8">
            <Spinner />
          </div>
        ) : jobs.length === 0 ? (
          <div className="text-center py-12">
            <Briefcase className="w-12 h-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-500">No jobs added yet</p>
          </div>
        ) : (
          <div className="space-y-4">
            {jobs.map((job) => (
              <div
                key={job.id}
                className="p-6 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
              >
                <div className="flex items-start justify-between mb-3">
                  <div className="flex-1">
                    <h3 className="text-lg font-semibold text-gray-900">{job.title}</h3>
                    {job.company && (
                      <p className="text-gray-600 mt-1">{job.company}</p>
                    )}
                  </div>
                  <button
                    onClick={() => handleDelete(job.id)}
                    className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>

                <div className="flex flex-wrap gap-2 mb-3">
                  {job.location && (
                    <Badge variant="info">
                      <MapPin className="w-3 h-3 inline mr-1" />
                      {job.location}
                    </Badge>
                  )}
                  {job.jobType && <Badge variant="default">{job.jobType}</Badge>}
                  {job.experienceLevel && <Badge variant="info">{job.experienceLevel}</Badge>}
                </div>

                <p className="text-sm text-gray-600 line-clamp-2 mb-3">
                  {job.description}
                </p>

                <div className="flex flex-wrap gap-2 mb-3">
                  <span className="text-sm font-medium text-gray-700">Required Skills:</span>
                  {job.requiredSkills.slice(0, 5).map((skill, index) => (
                    <Badge key={index} variant="success">
                      {skill}
                    </Badge>
                  ))}
                  {job.requiredSkills.length > 5 && (
                    <Badge variant="default">+{job.requiredSkills.length - 5} more</Badge>
                  )}
                </div>

                <p className="text-xs text-gray-500">Added {formatDate(job.createdAt)}</p>
              </div>
            ))}
          </div>
        )}
      </Card>
    </div>
  );
}
