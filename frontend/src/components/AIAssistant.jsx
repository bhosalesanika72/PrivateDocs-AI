import { useState } from "react";
import { Link } from "react-router-dom";

function AIAssistant() {
  const [question, setQuestion] = useState("");
  const [messages, setMessages] = useState([]);

  const handleAsk = () => {
    if (!question.trim()) {
      return;
    }

    const userMessage = {
      type: "user",
      text: question
    };

    const aiMessage = {
      type: "ai",
      text:
        "AI Assistant is ready. Connect your backend AI API here to get answers from your uploaded documents."
    };

    setMessages((previous) => [
      ...previous,
      userMessage,
      aiMessage
    ]);

    setQuestion("");
  };

  return (
    <div className="ai-page">

      <div className="page-header">

        <div>
          <p className="small-label">
            PRIVATE DOCUMENT AI
          </p>

          <h1>AI Assistant</h1>

          <p className="page-description">
            Ask questions and get help with your documents.
          </p>
        </div>

        <div className="header-icon">
          ✦
        </div>

      </div>

      <div className="ai-container">

        <div className="ai-welcome">
          <div className="ai-big-icon">
            ✦
          </div>

          <h2>How can I help?</h2>

          <p>
            Ask a question about your documents.
          </p>
        </div>

        <div className="chat-area">

          {messages.map((message, index) => (

            <div
              key={index}
              className={
                message.type === "user"
                  ? "message user-message"
                  : "message ai-message"
              }
            >
              {message.text}
            </div>

          ))}

        </div>

        <div className="question-box">

          <input
            type="text"
            placeholder="Ask something about your documents..."
            value={question}
            onChange={(event) =>
              setQuestion(event.target.value)
            }
            onKeyDown={(event) => {
              if (event.key === "Enter") {
                handleAsk();
              }
            }}
          />

          <button onClick={handleAsk}>
            Ask AI
          </button>

        </div>

      </div>

    </div>
  );
}

export default AIAssistant; 