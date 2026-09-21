import { Link } from "react-router-dom";

export default function Landing() {
  return (
    <div className="landing-page">
      <header className="landing-nav">
        <div className="app-brand">
          <div className="app-logo">◇</div>
          PrivateDocs AI
        </div>
        <div className="landing-nav-links">
          <a href="#how-it-works">How it works</a>
          <Link to="/login" className="ghost-button">Sign In</Link>
          <Link to="/register" className="primary-button small-button">Get Started</Link>
        </div>
      </header>

      <section className="landing-hero">
        <p className="small-label">PRIVATE DOCUMENT WORKSPACE</p>
        <h1>
          Your documents,
          <br />
          <span className="accent-text">understood by AI.</span>
        </h1>
        <p className="landing-subtitle">
          Upload your private PDFs, keep them organized in one secure workspace,
          and ask AI questions to get instant answers grounded in your own files.
        </p>
        <div className="landing-cta-group">
          <Link to="/register" className="primary-button cta-button">Get Started — it's free</Link>
          <a href="#how-it-works" className="ghost-button cta-button">Learn more</a>
        </div>
      </section>

      <section className="landing-features">
        <div className="landing-feature-card">
          <div className="card-icon">📤</div>
          <h2>Upload in seconds</h2>
          <p>Drag and drop your PDFs into your private workspace and they're ready to use right away.</p>
        </div>
        <div className="landing-feature-card">
          <div className="card-icon">🔐</div>
          <h2>Private by design</h2>
          <p>Your documents stay inside your own secure workspace, accessible only to you.</p>
        </div>
        <div className="landing-feature-card">
          <div className="card-icon">✦</div>
          <h2>Ask AI anything</h2>
          <p>Chat with an AI assistant that reads your documents and answers questions grounded in them.</p>
        </div>
      </section>

      <section id="how-it-works" className="landing-how">
        <p className="small-label">HOW IT WORKS</p>
        <h2>Three steps to smarter documents</h2>

        <div className="landing-steps">
          <div className="landing-step">
            <div className="step-number">1</div>
            <h3>Create your account</h3>
            <p>Sign up for free and get your own private document workspace in seconds.</p>
          </div>
          <div className="landing-step">
            <div className="step-number">2</div>
            <h3>Upload your PDFs</h3>
            <p>Add the documents you want to work with — contracts, notes, reports, anything.</p>
          </div>
          <div className="landing-step">
            <div className="step-number">3</div>
            <h3>Ask the AI Assistant</h3>
            <p>Get instant, accurate answers pulled directly from the content of your documents.</p>
          </div>
        </div>

        <div className="landing-cta-group centered">
          <Link to="/register" className="primary-button cta-button">Get Started — it's free</Link>
        </div>
      </section>

      <footer className="landing-footer">
        <p>© {new Date().getFullYear()} PrivateDocs AI. Already have an account? <Link to="/login">Sign in</Link>.</p>
      </footer>
    </div>
  );
}
