import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

const API_URL = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/$/, "");

export default function Register() {
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const handleRegister = async (e) => {
    e.preventDefault();
    setMessage("");

    if (!name.trim() || !email.trim() || !password) {
      setMessage("Name, email and password are required.");
      return;
    }

    if (password.length < 6) {
      setMessage("Password must be at least 6 characters.");
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`${API_URL}/api/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: name.trim(), email: email.trim(), password }),
      });

      const text = await response.text();
      let data;
      try { data = JSON.parse(text); } catch { data = { message: text }; }

      if (!response.ok) {
        throw new Error(data?.message || data?.error || text || "Registration failed.");
      }

      setMessage("Registration successful. Redirecting to login...");
      setTimeout(() => navigate("/login", { replace: true }), 900);
    } catch (error) {
      console.error("Registration error:", error);
      setMessage(error.message || "Unable to connect to the backend.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="logo-box">◇</div>
        <h1>PrivateDocs AI</h1>
        <p className="auth-subtitle">Secure AI Workspace</p>

        <h2>Create your account</h2>
        <p className="description">Create an account to securely manage your documents.</p>

        {message && <div className="message">{message}</div>}

        <form onSubmit={handleRegister}>
          <div className="input-group">
            <label>Full Name</label>
            <input value={name} placeholder="Your full name" onChange={(e) => setName(e.target.value)} autoComplete="name" />
          </div>

          <div className="input-group">
            <label>Email</label>
            <input type="email" value={email} placeholder="you@example.com" onChange={(e) => setEmail(e.target.value)} autoComplete="email" />
          </div>

          <div className="input-group">
            <label>Password</label>
            <input type="password" value={password} placeholder="Minimum 6 characters" onChange={(e) => setPassword(e.target.value)} autoComplete="new-password" />
          </div>

          <button className="primary-button" type="submit" disabled={loading}>
            {loading ? "Creating account..." : "Create Account"}
          </button>
        </form>

        <p className="switch-text">
          Already have an account? <Link to="/login">Sign In</Link>
        </p>
      </div>
    </div>
  );
}
