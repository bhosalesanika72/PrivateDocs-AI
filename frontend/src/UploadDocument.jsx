import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { saveDocument } from "./documentStore";

function UploadDocument() {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleFileChange = (event) => {
    const selectedFile = event.target.files[0];

    setError("");
    setMessage("");

    if (!selectedFile) {
      setFile(null);
      return;
    }

    if (selectedFile.type !== "application/pdf") {
      setFile(null);
      setError("Please select a PDF file only.");
      return;
    }

    setFile(selectedFile);

    setMessage(
      `"${selectedFile.name}" selected successfully.`
    );
  };

  const handleUpload = async () => {
    if (!file) {
      setError("Please select a PDF file first.");
      return;
    }

    try {
      await saveDocument(file);

      setMessage(
        `"${file.name}" uploaded successfully.`
      );

      setTimeout(() => {
        navigate("/documents");
      }, 800);

    } catch (err) {
      console.error(err);

      setError(
        "Unable to save the document. Please try again."
      );
    }
  };

  return (
    <div className="upload-page">

      <div className="upload-header">

        <div>
          <p className="small-label">
            PRIVATE DOCUMENT WORKSPACE
          </p>

          <h1>Upload Document</h1>

          <p>
            Add a private PDF to your workspace.
          </p>
        </div>

        <div className="upload-title-icon">
          📄
        </div>

      </div>

      <Link to="/" className="back-button">
        ← Dashboard
      </Link>

      <div className="upload-container">

        <div className="upload-icon">
          📄
        </div>

        <h2>Upload your PDF</h2>

        <p>
          Select a PDF file from your computer.
        </p>

        <div className="file-row">

          <label className="file-label">
            Choose PDF

            <input
              type="file"
              accept="application/pdf,.pdf"
              onChange={handleFileChange}
            />
          </label>

          <button
            className="upload-button"
            onClick={handleUpload}
          >
            Upload Document
          </button>

        </div>

        {file && (
          <div className="selected-file">
            <span>Selected:</span>
            <strong>{file.name}</strong>
          </div>
        )}

        {message && (
          <div className="success-message">
            {message}
          </div>
        )}

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

      </div>

    </div>
  );
}

export default UploadDocument;