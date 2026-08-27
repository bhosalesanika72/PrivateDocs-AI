import { useEffect, useState } from "react";
import "./App.css";

const API_URL = "https://privatedocs-ai.onrender.com";

function App() {
  const [activePage, setActivePage] = useState("dashboard");
  const [question, setQuestion] = useState("");
  const [answer, setAnswer] = useState("");
  const [loading, setLoading] = useState(false);

  const [selectedFile, setSelectedFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [uploadMessage, setUploadMessage] = useState("");

  const [documents, setDocuments] = useState([]);
  const [searchText, setSearchText] = useState("");

  // --------------------------------------------------
  // LOAD DOCUMENTS
  // --------------------------------------------------
  const loadDocuments = async () => {
    try {
      const response = await fetch(`${API_URL}/api/documents`);

      if (!response.ok) {
        return;
      }

      const data = await response.json();

      if (Array.isArray(data)) {
        setDocuments(data);
      } else if (data.content && Array.isArray(data.content)) {
        setDocuments(data.content);
      }
    } catch (error) {
      console.log("Could not load documents:", error);
    }
  };

  useEffect(() => {
    loadDocuments();
  }, []);

  // --------------------------------------------------
  // ASK AI
  // --------------------------------------------------
  const askAI = async () => {
    if (!question.trim()) {
      setAnswer("Please enter a question.");
      return;
    }

    setLoading(true);
    setAnswer("");

    try {
      const response = await fetch(`${API_URL}/api/ask`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          question: question,
        }),
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || "Unable to get answer.");
      }

      // Handle different possible backend response formats
      const result =
        data.answer ||
        data.response ||
        data.message ||
        data.result ||
        (typeof data === "string" ? data : JSON.stringify(data));

      setAnswer(result);
    } catch (error) {
      console.error(error);

      setAnswer(
        "Unable to connect to the AI service. Please make sure your Spring Boot backend is running on port 8080."
      );
    } finally {
      setLoading(false);
    }
  };

  // --------------------------------------------------
  // UPLOAD PDF
  // --------------------------------------------------
  const uploadPDF = async () => {
    if (!selectedFile) {
      setUploadMessage("Please select a PDF file first.");
      return;
    }

    if (selectedFile.type !== "application/pdf") {
      setUploadMessage("Only PDF files are allowed.");
      return;
    }

    const formData = new FormData();
    formData.append("file", selectedFile);

    setUploading(true);
    setUploadMessage("");

    try {
      const response = await fetch(`${API_URL}/api/documents/upload`, {
        method: "POST",
        body: formData,
      });

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(data.message || "Upload failed.");
      }

      setUploadMessage("PDF uploaded successfully.");
      setSelectedFile(null);

      const fileInput = document.getElementById("pdfInput");

      if (fileInput) {
        fileInput.value = "";
      }

      await loadDocuments();
    } catch (error) {
      console.error(error);

      setUploadMessage(
        "Upload failed. Check that the Spring Boot upload API is running."
      );
    } finally {
      setUploading(false);
    }
  };

  // --------------------------------------------------
  // FILTER DOCUMENTS
  // --------------------------------------------------
  const filteredDocuments = documents.filter((doc) => {
    const name =
      doc.name ||
      doc.fileName ||
      doc.filename ||
      doc.title ||
      "Untitled document";

    return name.toLowerCase().includes(searchText.toLowerCase());
  });

  // --------------------------------------------------
  // NEW CHAT
  // --------------------------------------------------
  const newChat = () => {
    setQuestion("");
    setAnswer("");
    setActivePage("dashboard");
  };

  // --------------------------------------------------
  // DASHBOARD
  // --------------------------------------------------
  const Dashboard = () => {
    return (
      <>
        <div className="welcome-section">
          <div>
            <h1>
              Welcome back, Sanika <span>👋</span>
            </h1>

            <p>
              Ask questions about your documents and get intelligent answers.
            </p>
          </div>
        </div>

        {/* ASK AI CARD */}
        <section className="ai-card">
          <div className="ai-card-header">
            <div className="ai-icon">✦</div>

            <div>
              <h2>Ask your documents</h2>
              <p>
                Your questions are answered using your uploaded documents.
              </p>
            </div>
          </div>

          <textarea
            className="question-box"
            placeholder="Ask AI a question about your documents..."
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter" && e.ctrlKey) {
                askAI();
              }
            }}
          />

          <div className="question-footer">
            <span>🔒 Private & Secure</span>

            <button
              className="primary-button"
              onClick={askAI}
              disabled={loading}
            >
              {loading ? "Thinking..." : "Ask AI →"}
            </button>
          </div>

          {answer && (
            <div className="answer-box">
              <div className="answer-title">
                <span>✨</span>
                AI Answer
              </div>

              <p>{answer}</p>
            </div>
          )}
        </section>

        {/* UPLOAD SECTION */}
        <section className="section">
          <div className="section-heading">
            <h2>Upload Document</h2>
            <p>Upload a PDF and let PrivateDocs AI understand it.</p>
          </div>

          <UploadBox />
        </section>

        {/* DOCUMENTS */}
        <section className="section">
          <div className="section-heading">
            <h2>Your Documents</h2>
            <p>Your uploaded documents will appear here.</p>
          </div>

          <DocumentList />
        </section>
      </>
    );
  };

  // --------------------------------------------------
  // UPLOAD BOX
  // --------------------------------------------------
  const UploadBox = () => {
    return (
      <div className="upload-card">
        <div className="upload-icon">↑</div>

        <h3>Upload your PDF</h3>

        <p>Select a PDF document from your computer</p>

        <div className="file-row">
          <input
            id="pdfInput"
            type="file"
            accept=".pdf,application/pdf"
            onChange={(e) => {
              setSelectedFile(e.target.files[0] || null);
              setUploadMessage("");
            }}
          />

          <button
            className="primary-button"
            onClick={uploadPDF}
            disabled={uploading}
          >
            {uploading ? "Uploading..." : "Upload PDF"}
          </button>
        </div>

        {selectedFile && (
          <div className="selected-file">
            📄 {selectedFile.name}
          </div>
        )}

        {uploadMessage && (
          <div
            className={
              uploadMessage.includes("successfully")
                ? "success-message"
                : "error-message"
            }
          >
            {uploadMessage}
          </div>
        )}
      </div>
    );
  };

  // --------------------------------------------------
  // DOCUMENT LIST
  // --------------------------------------------------
  const DocumentList = () => {
    if (filteredDocuments.length === 0) {
      return (
        <div className="empty-state">
          <div className="empty-icon">📄</div>

          <h3>No documents found</h3>

          <p>
            Upload a PDF document to start asking questions about it.
          </p>
        </div>
      );
    }

    return (
      <div className="document-grid">
        {filteredDocuments.map((doc, index) => {
          const name =
            doc.name ||
            doc.fileName ||
            doc.filename ||
            doc.title ||
            `Document ${index + 1}`;

          return (
            <div className="document-card" key={doc.id || index}>
              <div className="document-icon">📄</div>

              <div className="document-info">
                <h3>{name}</h3>

                <p>PDF Document</p>
              </div>

              <button className="more-button">⋮</button>
            </div>
          );
        })}
      </div>
    );
  };

  // --------------------------------------------------
  // DOCUMENTS PAGE
  // --------------------------------------------------
  const DocumentsPage = () => {
    return (
      <div>
        <div className="page-title">
          <h1>Documents</h1>
          <p>Manage your uploaded documents.</p>
        </div>

        <div className="search-large">
          <span>⌕</span>

          <input
            type="text"
            placeholder="Search documents..."
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
          />
        </div>

        <UploadBox />

        <div className="section">
          <DocumentList />
        </div>
      </div>
    );
  };

  // --------------------------------------------------
  // AI ASSISTANT PAGE
  // --------------------------------------------------
  const AssistantPage = () => {
    return (
      <div>
        <div className="page-title">
          <h1>AI Assistant ✨</h1>
          <p>
            Ask questions and get intelligent answers from your documents.
          </p>
        </div>

        <section className="ai-card assistant-card">
          <div className="ai-card-header">
            <div className="ai-icon">✦</div>

            <div>
              <h2>Ask PrivateDocs AI</h2>
              <p>Your documents are your AI knowledge base.</p>
            </div>
          </div>

          <textarea
            className="question-box"
            placeholder="Type your question here..."
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
          />

          <div className="question-footer">
            <span>🔒 Your documents stay private</span>

            <button
              className="primary-button"
              onClick={askAI}
              disabled={loading}
            >
              {loading ? "Thinking..." : "Ask AI →"}
            </button>
          </div>

          {answer && (
            <div className="answer-box">
              <div className="answer-title">
                ✨ AI Answer
              </div>

              <p>{answer}</p>
            </div>
          )}
        </section>
      </div>
    );
  };

  // --------------------------------------------------
  // SETTINGS PAGE
  // --------------------------------------------------
  const SettingsPage = () => {
    return (
      <div>
        <div className="page-title">
          <h1>Settings</h1>
          <p>Manage your PrivateDocs AI preferences.</p>
        </div>

        <div className="settings-card">
          <div className="setting-row">
            <div>
              <h3>AI Assistant</h3>
              <p>Allow PrivateDocs AI to answer your questions.</p>
            </div>

            <div className="toggle active">
              <div></div>
            </div>
          </div>

          <div className="setting-row">
            <div>
              <h3>Private Mode</h3>
              <p>Keep your document conversations private.</p>
            </div>

            <div className="toggle active">
              <div></div>
            </div>
          </div>

          <div className="setting-row">
            <div>
              <h3>Account</h3>
              <p>Sanika Bhosale</p>
            </div>

            <button className="secondary-button">
              Manage
            </button>
          </div>
        </div>
      </div>
    );
  };

  // --------------------------------------------------
  // MAIN UI
  // --------------------------------------------------
  return (
    <div className="app">
      {/* SIDEBAR */}
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-logo">◇</div>

          <div>
            <h2>PrivateDocs AI</h2>
            <p>Secure AI Workspace</p>
          </div>
        </div>

        <div className="sidebar-search">
          <span>⌕</span>

          <input
            type="text"
            placeholder="Search documents..."
            value={searchText}
            onChange={(e) => {
              setSearchText(e.target.value);

              if (e.target.value.length > 0) {
                setActivePage("documents");
              }
            }}
          />
        </div>

        <nav className="navigation">
          <button
            className={`nav-item ${
              activePage === "dashboard" ? "active" : ""
            }`}
            onClick={() => setActivePage("dashboard")}
          >
            <span>⌂</span>
            Dashboard
          </button>

          <button
            className={`nav-item ${
              activePage === "documents" ? "active" : ""
            }`}
            onClick={() => setActivePage("documents")}
          >
            <span>▤</span>
            Documents
          </button>

          <button
            className={`nav-item ${
              activePage === "assistant" ? "active" : ""
            }`}
            onClick={() => setActivePage("assistant")}
          >
            <span>✦</span>
            AI Assistant
          </button>

          <button
            className={`nav-item ${
              activePage === "settings" ? "active" : ""
            }`}
            onClick={() => setActivePage("settings")}
          >
            <span>⚙</span>
            Settings
          </button>
        </nav>

        <div className="sidebar-bottom">
          <div className="profile-avatar">S</div>

          <div>
            <strong>Sanika</strong>
            <span>Personal Plan</span>
          </div>

          <button className="profile-menu">⋮</button>
        </div>
      </aside>

      {/* MAIN CONTENT */}
      <main className="main">
        <header className="topbar">
          <div>
            <h1>PrivateDocs AI</h1>
            <p>Your private AI-powered document assistant</p>
          </div>

          <div className="topbar-actions">
            <div className="status">
              <span></span>
              AI Online
            </div>

            <button className="new-chat-button" onClick={newChat}>
              + New Chat
            </button>
          </div>
        </header>

        <div className="content">
          {activePage === "dashboard" && <Dashboard />}

          {activePage === "documents" && <DocumentsPage />}

          {activePage === "assistant" && <AssistantPage />}

          {activePage === "settings" && <SettingsPage />}
        </div>
      </main>
    </div>
  );
}

export default App;