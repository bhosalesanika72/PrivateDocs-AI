import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

const API_URL = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/$/, "");

export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");

    if (!email.trim() || !password) {
      setMessage("Email and password are required.");
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`${API_URL}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email.trim(), password }),
      });

      const text = await response.text();
      let data;
      try { data = JSON.parse(text); } catch { data = { message: text }; }

      if (!response.ok) {
        throw new Error(data?.message || data?.error || text || "Login failed.");
      }

      if (!data?.token) {
        throw new Error("Login succeeded but the server did not return a JWT token.");
      }

      localStorage.setItem("token", data.token);
      localStorage.setItem("userName", data.name || "User");
      localStorage.setItem("userEmail", data.email || email.trim());

      navigate("/dashboard", { replace: true });
    } catch (error) {
      console.error("Login error:", error);
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

        <h2>Welcome back</h2>
        <p className="description">Sign in to your secure document workspace.</p>

        {message && <div className="message error">{message}</div>}

        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label>Email</label>
            <input
              type="email"
              value={email}
              placeholder="you@example.com"
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="email"
            />
          </div>

          <div className="input-group">
            <label>Password</label>
            <input
              type="password"
              value={password}
              placeholder="Enter your password"
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
            />
          </div>

          <button className="primary-button" type="submit" disabled={loading}>
            {loading ? "Signing in..." : "Sign In"}
          </button>
        </form>

        <p className="switch-text">
          Don't have an account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  );
}
