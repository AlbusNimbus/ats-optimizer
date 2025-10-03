import { useEffect, useState } from 'react';
import { useStore } from '../store/useStore';
import { documentService } from '../services/documentService';
import FileUpload from '../components/upload/FileUpload';
import Card from '../components/common/Card';
import Badge from '../components/common/Badge';
import Spinner from '../components/common/Spinner';
import { formatDate, formatFileSize } from '../utils/formatters';
import { File, Trash2 } from 'lucide-react';

export default function Documents() {
  const { userId, documents, setDocuments } = useStore();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDocuments();
  }, []);

  const loadDocuments = async () => {
    try {
      const docs = await documentService.getUserDocuments(userId);
      setDocuments(docs);
    } catch (error) {
      console.error('Error loading documents:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (documentId: number) => {
    if (!confirm('Are you sure you want to delete this resume?')) return;

    try {
      await documentService.deleteDocument(documentId);
      setDocuments(documents.filter(d => d.id !== documentId));
    } catch (error) {
      console.error('Error deleting document:', error);
      alert('Failed to delete document');
    }
  };

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">My Resumes</h1>
        <p className="text-gray-600 mt-2">Upload and manage your resumes</p>
      </div>

      <FileUpload />

      <Card title={`Your Resumes (${documents.length})`}>
        {loading ? (
          <div className="flex justify-center py-8">
            <Spinner />
          </div>
        ) : documents.length === 0 ? (
          <div className="text-center py-12">
            <File className="w-12 h-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-500">No resumes uploaded yet</p>
          </div>
        ) : (
          <div className="space-y-3">
            {documents.map((doc) => (
              <div
                key={doc.id}
                className="flex items-center justify-between p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
              >
                <div className="flex items-center space-x-4">
                  <div className="w-10 h-10 bg-primary-100 rounded-lg flex items-center justify-center">
                    <File className="w-5 h-5 text-primary-600" />
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">{doc.fileName}</p>
                    <p className="text-sm text-gray-500">
                      {formatDate(doc.createdAt)} • {formatFileSize(doc.fileSizeBytes)}
                    </p>
                  </div>
                </div>
                <div className="flex items-center space-x-3">
                  <Badge variant={
                    doc.status === 'COMPLETED' ? 'success' :
                    doc.status === 'PROCESSING' ? 'warning' :
                    doc.status === 'FAILED' ? 'error' : 'default'
                  }>
                    {doc.status}
                  </Badge>
                  <button
                    onClick={() => handleDelete(doc.id)}
                    className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </Card>
    </div>
  );
}
