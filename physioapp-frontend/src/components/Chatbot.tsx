import React, { useState } from 'react';
import styles from './Chatbot.module.css';
import BotProfilePic from '../assets/Bot.png';

interface Message {
  sender: 'user' | 'bot';
  text: string;
}

const Chatbot: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([
    {
      sender: 'bot',
      text: "Hello! I'm Dr. Sarah Chen, your AI physiotherapy assistant. I'm here to help you with exercise guidance, injury recovery advice, and general physiotherapy questions. How can I assist you today?"
    }
  ]);
  const [input, setInput] = useState('');
  const [isBotTyping, setIsBotTyping] = useState(false);
  const [selectedModel, setSelectedModel] = useState('deepseek');

  const sendMessage = async () => {
    if (!input.trim()) return;
    const userMsg: Message = { sender: 'user', text: input };
    setMessages(prev => [...prev, userMsg]);
    setInput('');
    setIsBotTyping(true);

    try {
      const res = await fetch(`http://localhost:8080/api/chat/send`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userMsg.text)
      });
      const data = await res.text();
      setMessages(prev => [...prev, { sender: 'bot', text: data }]);
    } catch (e) {
      console.error("Error contacting bot:", e);
      setMessages(prev => [...prev, { 
        sender: 'bot', 
        text: 'I apologize, but I\'m having trouble connecting right now. Please try again in a moment, or if your question is urgent, consider consulting with a healthcare professional.' 
      }]);
    } finally {
      setIsBotTyping(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <img src={BotProfilePic} alt="Dr. Sarah Chen" className={styles.headerProfilePic} />
        <div className={styles.headerInfo}>
          <h3>Dr. Sarah Chen, PT, DPT</h3>
          <p>AI Physiotherapy Assistant</p>
          <small>Licensed Physiotherapist • 15+ Years Experience</small>
        </div>
      </div>
      
      <div className={styles.messages}>
        {messages.map((m, i) => (
          <div key={i} className={m.sender === 'user' ? styles.user : styles.bot}>
            {m.sender === 'bot' && <img src={BotProfilePic} alt="Dr. Sarah Chen" className={styles.profilePhoto} />}
            <div className={styles.messageBubble}>
              {m.text}
              {m.sender === 'bot' && (
                <div className={styles.disclaimer}>
                  <small>💡 This is AI guidance only. For serious concerns, please consult a healthcare professional.</small>
                </div>
              )}
            </div>
            {m.sender === 'user' && <div className={styles.profilePhoto}></div>}
          </div>
        ))}
        {isBotTyping && (
          <div className={styles.typingIndicator}>
            <img src={BotProfilePic} alt="Dr. Sarah Chen" className={styles.profilePhoto} />
            <div className={styles.typingText}>
              Dr. Chen is typing<span>.</span><span>.</span><span>.</span>
            </div>
          </div>
        )}
      </div>
      
      <div className={styles.inputArea}>
        <div className={styles.modelSelection}>
          <button
            className={selectedModel === 'deepseek' ? styles.selectedModelButton : styles.modelButton}
            onClick={() => setSelectedModel('deepseek')}
          >
            DeepSeek AI
          </button>
          <button
            className={selectedModel === 'gemini' ? styles.selectedModelButton : styles.modelButton}
            onClick={() => setSelectedModel('gemini')}
          >
            Gemini AI
          </button>
        </div>
        <div className={styles.inputContainer}>
          <input 
            value={input} 
            onChange={e => setInput(e.target.value)} 
            className={styles.input} 
            placeholder="Ask me about exercises, injuries, or rehabilitation..."
            onKeyPress={event => {
              if (event.key === 'Enter') {
                sendMessage();
              }
            }} 
          />
          <button onClick={sendMessage} className={styles.send} disabled={!input.trim()}>
            Send
          </button>
        </div>
        <div className={styles.safetyNotice}>
          <small>⚠️ For emergencies or severe pain, please contact emergency services or your healthcare provider immediately.</small>
        </div>
      </div>
    </div>
  );
};

export default Chatbot;
