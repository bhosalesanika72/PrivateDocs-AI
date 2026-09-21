const API_URL = "http://localhost:8080/api";

function authHeaders() {
  const token = localStorage.getItem("token");
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function readResponse(response) {
  const text = await response.text();
  let data;
  try { data = text ? JSON.parse(text) : null; } catch { data = text; }
  if (!response.ok) {
    const message = typeof data === "string" ? data : data?.message || data?.error;
    throw new Error(message || `Request failed (${response.status})`);
  }
  return data;
}

export async function getDocuments() {
  const response = await fetch(`${API_URL}/documents`, {
    headers: authHeaders(),
  });
  return readResponse(response);
}

export async function uploadDocument(file) {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${API_URL}/documents/upload`, {
    method: "POST",
    headers: authHeaders(),
    body: formData,
  });
  return readResponse(response);
}

export async function deleteDocument(id) {
  const response = await fetch(`${API_URL}/documents/${id}`, {
    method: "DELETE",
    headers: authHeaders(),
  });
  await readResponse(response);
  return true;
}

export async function askAI(question) {
  const response = await fetch(`${API_URL}/ask`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...authHeaders(),
    },
    body: JSON.stringify({ question }),
  });
  return readResponse(response);
}
