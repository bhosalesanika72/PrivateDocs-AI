const STORAGE_KEY = "privateDocs_documents";

function getDocuments() {
  try {
    const documents = localStorage.getItem(STORAGE_KEY);

    if (!documents) {
      return [];
    }

    return JSON.parse(documents);

  } catch (error) {
    console.error("Error reading documents:", error);
    return [];
  }
}

function setDocuments(documents) {
  localStorage.setItem(
    STORAGE_KEY,
    JSON.stringify(documents)
  );
}

export function saveDocument(file) {
  return new Promise((resolve, reject) => {

    const reader = new FileReader();

    reader.onload = () => {

      const documents = getDocuments();

      const documentData = {
        id: Date.now().toString(),
        name: file.name,
        size: file.size,
        type: file.type,
        data: reader.result,
        uploadedAt: new Date().toISOString()
      };

      documents.push(documentData);

      setDocuments(documents);

      resolve(documentData);
    };

    reader.onerror = () => {
      reject(new Error("Could not read the PDF file."));
    };

    reader.readAsDataURL(file);
  });
}

export function getAllDocuments() {
  return getDocuments();
}

export function deleteDocument(id) {
  const documents = getDocuments();

  const updatedDocuments = documents.filter(
    (document) => document.id !== id
  );

  setDocuments(updatedDocuments);
}

export function getDocument(id) {
  const documents = getDocuments();

  return documents.find(
    (document) => document.id === id
  );
}