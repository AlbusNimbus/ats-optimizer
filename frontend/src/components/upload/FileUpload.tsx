import { useCallback, useState } from 'react';
import { useDropzone } from 'react-dropzone';
import { Upload, File, X } from 'lucide-react';
import Button from '../common/Button';
import { documentService } from '../../services/documentService';
import { useStore } from '../../store/useStore';

export default function FileUpload() {
  const [uploading, setUploading] = useState(false);
  const [uploadedFile, setUploadedFile] = useState<File | null>(null);
  const { userId, addDocument } = useStore();

  const onDrop = useCallback((acceptedFiles: File[]) => {
    if (acceptedFiles.length > 0) {
      setUploadedFile(acceptedFiles[0]);
    }
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'application/pdf': ['.pdf'],
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document': ['.docx'],
      'application/msword': ['.doc'],
    },
    maxFiles: 1,
  });

  const handleUpload = async () => {
    if (!uploadedFile) return;

    setUploading(true);
    try {
      const response = await documentService.uploadDocument(uploadedFile, userId);
      
      // Fetch the full document details
      const document = await documentService.getDocument(response.id);
      addDocument(document);
      
      alert('Resume uploaded successfully!');
      setUploadedFile(null);
    } catch (error) {
      console.error('Upload error:', error);
      alert('Failed to upload resume. Please try again.');
    } finally {
      setUploading(false);
    }
  };

  const removeFile = () => {
    setUploadedFile(null);
  };

  return (
    <div className="card">
      <h2 className="text-2xl font-bold mb-4">Upload Resume</h2>
      
      {!uploadedFile ? (
        <div
          {...getRootProps()}
          className={`border-2 border-dashed rounded-lg p-12 text-center cursor-pointer transition-colors ${
            isDragActive
              ? 'border-primary-500 bg-primary-50'
              : 'border-gray-300 hover:border-primary-400'
          }`}
        >
          <input {...getInputProps()} />
          <Upload className="w-12 h-12 mx-auto mb-4 text-gray-400" />
          <p className="text-lg font-medium text-gray-700 mb-2">
            {isDragActive ? 'Drop your resume here' : 'Drag & drop your resume here'}
          </p>
          <p className="text-sm text-gray-500 mb-4">or click to browse</p>
          <p className="text-xs text-gray-400">Supported formats: PDF, DOCX, DOC (Max 10MB)</p>
        </div>
      ) : (
        <div className="border-2 border-gray-200 rounded-lg p-6">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-primary-100 rounded-lg flex items-center justify-center">
                <File className="w-5 h-5 text-primary-600" />
              </div>
              <div>
                <p className="font-medium text-gray-900">{uploadedFile.name}</p>
                <p className="text-sm text-gray-500">
                  {(uploadedFile.size / 1024 / 1024).toFixed(2)} MB
                </p>
              </div>
            </div>
            <button
              onClick={removeFile}
              className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            >
              <X className="w-5 h-5 text-gray-500" />
            </button>
          </div>
          
          <Button 
            onClick={handleUpload} 
            isLoading={uploading}
            className="w-full"
          >
            Upload Resume
          </Button>
        </div>
      )}
    </div>
  );
}
