const API_URL = "https://privatedocs-ai.onrender.com/api";

// GET ALL DOCUMENTS
export async function getDocuments() {
  const response = await fetch(`${API_URL}/documents`);

  if (!response.ok) {
    throw new Error("Unable to load documents");
  }

  return await response.json();
}

// UPLOAD PDF
export async function uploadDocument(file) {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${API_URL}/documents/upload`, {
    method: "POST",
    body: formData,
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Upload failed");
  }

  return await response.json();
}

// DELETE DOCUMENT
export async function deleteDocument(id) {
  const response = await fetch(`${API_URL}/documents/${id}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    throw new Error("Unable to delete document");
  }

  return true;
}

// ASK AI
export async function askAI(question) {
  const response = await fetch(`${API_URL}/ask`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      question: question,
    }),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "AI request failed");
  }

  return await response.json();
}