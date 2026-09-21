import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { deleteDocument, getDocuments } from "./api";

export default function Documents() {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");

  useEffect(() => {
    let active = true;

    const loadDocuments = async () => {
      setLoading(true);
      try {
        const data = await getDocuments();
        if (active) {
          setDocuments(Array.isArray(data) ? data : []);
          setMessage("");
        }
      } catch (error) {
        if (active) setMessage(error.message || "Unable to load documents.");
      } finally {
        if (active) setLoading(false);
      }
    };

    loadDocuments();
    return () => { active = false; };
  }, []);

  const remove = async (id) => {
    if (!window.confirm("Are you sure you want to delete this document?")) return;
    try {
      await deleteDocument(id);
      setDocuments((items) => items.filter((doc) => doc.id !== id));
      setMessage("Document deleted successfully.");
    } catch (error) {
      setMessage(error.message || "Unable to delete document.");
    }
  };

  return (
    <section className="content-page">
      <div className="page-header documents-header">
        <div>
          <p className="small-label">PRIVATE DOCUMENT WORKSPACE</p>
          <h1>Your Documents</h1>
          <p className="page-description">Manage your uploaded documents in one secure place.</p>
        </div>
        <Link to="/upload" className="primary-button small-button">+ Upload Document</Link>
      </div>

      {message && <div className="message">{message}</div>}

      {loading ? (
        <div className="empty-state"><h2>Loading documents...</h2></div>
      ) : documents.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">📄</div>
          <h2>No documents yet</h2>
          <p>Upload your first PDF to start using PrivateDocs AI.</p>
          <Link to="/upload" className="primary-button small-button">Upload PDF</Link>
        </div>
      ) : (
        <div className="documents-grid">
          {documents.map((doc) => (
            <article className="document-card" key={doc.id}>
              <div className="document-icon">📄</div>
              <div className="document-content">
                <h2>{doc.fileName || doc.filename || doc.name || "Untitled Document"}</h2>
                <p>{doc.createdAt ? new Date(doc.createdAt).toLocaleDateString() : "Uploaded document"}</p>
              </div>
              <button className="delete-button" onClick={() => remove(doc.id)}>Delete</button>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}
