import { useState } from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";

export default function Layout() {
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const name = localStorage.getItem("userName") || "User";

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userName");
    localStorage.removeItem("userEmail");
    setOpen(false);
    navigate("/login", { replace: true });
  };

  const close = () => setOpen(false);

  return (
    <div className="app-shell">
      <header className="app-navbar">
        <div className="app-brand" onClick={() => navigate("/dashboard")}>
          <span className="app-logo">◇</span>
          <span>PrivateDocs AI</span>
        </div>

        <button className="menu-button" onClick={() => setOpen(!open)} aria-label="Open menu">
          ☰
        </button>

        <nav className={`app-nav ${open ? "open" : ""}`}>
          <NavLink to="/dashboard" onClick={close}>Dashboard</NavLink>
          <NavLink to="/documents" onClick={close}>Documents</NavLink>
          <NavLink to="/upload" onClick={close}>Upload</NavLink>
          <NavLink to="/ai" onClick={close}>AI Assistant</NavLink>
          <span className="user-name">{name}</span>
          <button className="logout-button" onClick={logout}>Logout</button>
        </nav>
      </header>
      <main className="app-content">
        <Outlet />
      </main>
    </div>
  );
}
