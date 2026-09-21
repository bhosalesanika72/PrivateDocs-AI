import { useState } from "react";
import { askAI } from "./api";

export default function AI() {
  const [question, setQuestion] = useState("");
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    const value = question.trim();
    if (!value || loading) return;

    setMessages((items) => [...items, { role: "user", text: value }]);
    setQuestion("");
    setLoading(true);

    try {
      const data = await askAI(value);
      setMessages((items) => [
        ...items,
        { role: "ai", text: data?.answer || "No answer was returned." },
      ]);
    } catch (error) {
      setMessages((items) => [
        ...items,
        { role: "ai", text: error.message || "AI request failed." },
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="content-page">
      <div className="page-header">
        <div>
          <p className="small-label">PRIVATE DOCUMENT AI</p>
          <h1>AI Assistant</h1>
          <p className="page-description">Ask questions about your uploaded documents.</p>
        </div>
        <div className="header-icon">✦</div>
      </div>

      <div className="ai-container">
        <div className="chat-area">
          {messages.length === 0 ? (
            <div className="empty-chat">
              <div className="large-icon">✦</div>
              <h2>How can I help?</h2>
              <p>Ask a question about your uploaded documents.</p>
            </div>
          ) : (
            messages.map((message, index) => (
              <div key={index} className={`chat-message ${message.role === "user" ? "user-message" : "ai-message"}`}>
                <strong>{message.role === "user" ? "You" : "AI Assistant"}</strong>
                <p>{message.text}</p>
              </div>
            ))
          )}
          {loading && <div className="chat-message ai-message"><strong>AI Assistant</strong><p>Thinking...</p></div>}
        </div>

        <form className="chat-form" onSubmit={submit}>
          <input
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
            placeholder="Ask something about your documents..."
          />
          <button className="primary-button" type="submit" disabled={loading}>
            {loading ? "Thinking..." : "Send"}
          </button>
        </form>
      </div>
    </section>
  );
}
