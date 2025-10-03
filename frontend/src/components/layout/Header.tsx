import { Link } from 'react-router-dom';
import { FileText, Briefcase, BarChart3 } from 'lucide-react';

export default function Header() {
  return (
    <header className="bg-white border-b border-gray-200 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <Link to="/" className="flex items-center space-x-3">
            <div className="w-10 h-10 bg-primary-600 rounded-lg flex items-center justify-center">
              <FileText className="w-6 h-6 text-white" />
            </div>
            <span className="text-xl font-bold text-gray-900">ATS Optimizer</span>
          </Link>

          <nav className="flex space-x-8">
            <Link 
              to="/" 
              className="text-gray-600 hover:text-primary-600 transition-colors font-medium"
            >
              Dashboard
            </Link>
            <Link 
              to="/documents" 
              className="text-gray-600 hover:text-primary-600 transition-colors font-medium flex items-center"
            >
              <FileText className="w-4 h-4 mr-1" />
              Resumes
            </Link>
            <Link 
              to="/jobs" 
              className="text-gray-600 hover:text-primary-600 transition-colors font-medium flex items-center"
            >
              <Briefcase className="w-4 h-4 mr-1" />
              Jobs
            </Link>
            <Link 
              to="/analyses" 
              className="text-gray-600 hover:text-primary-600 transition-colors font-medium flex items-center"
            >
              <BarChart3 className="w-4 h-4 mr-1" />
              Analyses
            </Link>
          </nav>
        </div>
      </div>
    </header>
  );
}
