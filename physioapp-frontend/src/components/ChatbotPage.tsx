import React from 'react';

const ChatbotPage: React.FC = () => {
  return (
    <div className="chatbot-container">
      <h1>Chatbot</h1>
      <div className="message-area">
        {/* Chat messages will go here */}
        <p>Bot: Hello! How can I help you today?</p>
        <p>User: Hi there!</p>
      </div>
      <div className="input-area">
        <input type="text" placeholder="Type your message..." />
        <button>Send</button>
      </div>
    </div>
  );
};

export default ChatbotPage; 