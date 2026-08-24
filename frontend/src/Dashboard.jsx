import { Link } from "react-router-dom";

function Dashboard() {
  return (
    <div className="dashboard-page">

      <div className="page-header">
        <div>
          <p className="small-label">PRIVATE DOCUMENT WORKSPACE</p>

          <h1>
            Your documents,
            <br />
            <span>smarter with AI.</span>
          </h1>

          <p className="page-description">
            Upload your private PDFs, manage your documents,
            and interact with them using AI.
          </p>
        </div>

        <div className="header-icon">
          ✦
        </div>
      </div>

      <div className="dashboard-cards">

        <Link to="/upload" className="dashboard-card">
          <div className="card-icon">📤</div>

          <h2>Upload Document</h2>

          <p>
            Add a PDF document to your private workspace.
          </p>

          <span className="card-link">
            Upload PDF →
          </span>
        </Link>

        <Link to="/documents" className="dashboard-card">
          <div className="card-icon">📄</div>

          <h2>Documents</h2>

          <p>
            View and manage all your uploaded documents.
          </p>

          <span className="card-link">
            View Documents →
          </span>
        </Link>

        <Link to="/ai" className="dashboard-card">
          <div className="card-icon">✦</div>

          <h2>AI Assistant</h2>

          <p>
            Ask questions and get help with your documents.
          </p>

          <span className="card-link">
            Ask AI →
          </span>
        </Link>

      </div>

      <div className="dashboard-info">

        <div>
          <span className="info-icon">🔐</span>

          <div>
            <h3>Private by Design</h3>

            <p>
              Your documents are handled inside your private
              workspace.
            </p>
          </div>
        </div>

        <div>
          <span className="info-icon">🤖</span>

          <div>
            <h3>AI Powered</h3>

            <p>
              Built to make your documents easier to understand.
            </p>
          </div>
        </div>

      </div>

    </div>
  );
}

export default Dashboard;