import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  getAllDocuments,
  deleteDocument
} from "./documentStore";

function Documents() {
  const [documents, setDocuments] = useState([]);

  const loadDocuments = () => {
    setDocuments(getAllDocuments());
  };

  useEffect(() => {
    loadDocuments();
  }, []);

  const handleDelete = (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this document?"
    );

    if (!confirmed) {
      return;
    }

    deleteDocument(id);

    loadDocuments();
  };

  const formatSize = (bytes) => {
    if (!bytes) return "0 KB";

    const kb = bytes / 1024;

    if (kb < 1024) {
      return `${kb.toFixed(1)} KB`;
    }

    return `${(kb / 1024).toFixed(1)} MB`;
  };

  const formatDate = (date) => {
    return new Date(date).toLocaleDateString();
  };

  return (
    <div className="documents-page">

      <div className="page-header">

        <div>
          <p className="small-label">
            YOUR PRIVATE LIBRARY
          </p>

          <h1>Documents</h1>

          <p className="page-description">
            Manage the PDF documents stored in your workspace.
          </p>
        </div>

        <Link
          to="/upload"
          className="primary-button"
        >
          + Upload PDF
        </Link>

      </div>

      {documents.length === 0 ? (

        <div className="empty-state">

          <div className="empty-icon">
            📄
          </div>

          <h2>No documents yet</h2>

          <p>
            Upload your first PDF to start using PrivateDocs.
          </p>

          <Link
            to="/upload"
            className="primary-button"
          >
            Upload Your First PDF
          </Link>

        </div>

      ) : (

        <div className="documents-grid">

          {documents.map((document) => (

            <div
              className="document-card"
              key={document.id}
            >

              <div className="document-icon">
                📄
              </div>

              <div className="document-info">

                <h3 title={document.name}>
                  {document.name}
                </h3>

                <p>
                  {formatSize(document.size)}
                  {" • "}
                  {formatDate(document.uploadedAt)}
                </p>

              </div>

              <div className="document-actions">

                <a
                  href={document.data}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="view-button"
                >
                  Open PDF
                </a>

                <button
                  className="delete-button"
                  onClick={() =>
                    handleDelete(document.id)
                  }
                >
                  Delete
                </button>

              </div>

            </div>

          ))}

        </div>

      )}

    </div>
  );
}

export default Documents;