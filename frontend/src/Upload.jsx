import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { uploadDocument } from "./api";

export default function Upload() {
  const navigate = useNavigate();
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const submit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    if (!file) return setError("Please select a PDF file.");
    if (file.type !== "application/pdf" && !file.name.toLowerCase().endsWith(".pdf")) {
      return setError("Only PDF files are allowed.");
    }

    setLoading(true);
    try {
      await uploadDocument(file);
      setMessage("PDF uploaded successfully.");
      setFile(null);
      setTimeout(() => navigate("/documents"), 800);
    } catch (err) {
      setError(err.message || "Upload failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="content-page">
      <div className="page-header">
        <div>
          <p className="small-label">PRIVATE DOCUMENT WORKSPACE</p>
          <h1>Upload Document</h1>
          <p className="page-description">Add a private PDF to your workspace.</p>
        </div>
        <div className="header-icon">📄</div>
      </div>

      <div className="upload-container">
        <div className="upload-icon">📄</div>
        <h2>Upload your PDF</h2>
        <p>Select a PDF file from your computer.</p>

        <form onSubmit={submit}>
          <label className="file-box">
            <span className="large-icon">📤</span>
            <strong>{file ? file.name : "Choose a PDF file"}</strong>
            <span>PDF files only</span>
            <input type="file" accept="application/pdf,.pdf" onChange={(e) => setFile(e.target.files?.[0] || null)} />
          </label>

          {message && <div className="message success">{message}</div>}
          {error && <div className="message error">{error}</div>}

          <button className="primary-button full-button" type="submit" disabled={loading}>
            {loading ? "Uploading..." : "Upload PDF"}
          </button>
        </form>
      </div>
    </section>
  );
}
