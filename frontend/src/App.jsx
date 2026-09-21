import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import "./App.css";

import Landing from "./Landing";
import Login from "./components/Login";
import Register from "./Register";
import Dashboard from "./Dashboard";
import Documents from "./Documents";
import Upload from "./Upload";
import AI from "./AI";
import Layout from "./Layout";

function ProtectedRoute({ children }) {
  const token = localStorage.getItem("token");

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Landing />} />

        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route
          element={
            <ProtectedRoute>
              <Layout />
            </ProtectedRoute>
          }
        >
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/documents" element={<Documents />} />
          <Route path="/upload" element={<Upload />} />
          <Route path="/ai" element={<AI />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
