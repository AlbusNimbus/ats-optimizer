import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import Layout from './components/layout/Layout';
import Dashboard from './pages/Dashboard';
import Documents from './pages/Documents';
import Jobs from './pages/Jobs';
import Analyze from './pages/Analyze';
import Analyses from './pages/Analyses';
import AnalysisDetail from './pages/AnalysisDetail';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <Layout>
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/documents" element={<Documents />} />
            <Route path="/jobs" element={<Jobs />} />
            <Route path="/analyze" element={<Analyze />} />
            <Route path="/analyses" element={<Analyses />} />
            <Route path="/analyses/:id" element={<AnalysisDetail />} />
          </Routes>
        </Layout>
      </Router>
    </QueryClientProvider>
  );
}
