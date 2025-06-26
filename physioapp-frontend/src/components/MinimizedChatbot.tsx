import React, { useState } from 'react';
import styles from './MinimizedChatbot.module.css';
import BotProfilePic from '../assets/Bot.png';

interface Message {
  sender: 'user' | 'bot';
  text: string;
}

const MinimizedChatbot: React.FC = () => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState('');
  const [isBotTyping, setIsBotTyping] = useState(false);
  const [selectedModel, setSelectedModel] = useState('deepseek');

  const sendMessage = async () => {
    if (!input) return;
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
      setMessages(prev => [...prev, { sender: 'bot', text: 'Error contacting bot' }]);
    } finally {
      setIsBotTyping(false);
    }
  };

  const handleKeyPress = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter') {
      sendMessage();
    }
  };

  if (!isExpanded) {
    return (
      <div className={styles.minimizedContainer}>
        <div 
          className={styles.minimizedChatbox}
          onClick={() => setIsExpanded(true)}
        >
          <div className={styles.botAvatar}>
            <img src={BotProfilePic} alt="Bot" className={styles.botImage} />
            <div className={styles.sleepingIndicator}>💤</div>
          </div>
          <div className={styles.minimizedText}>
            <div className={styles.botName}>AI Assistant</div>
            <div className={styles.sleepingText}>Click to wake me up...</div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.expandedContainer}>
      <div className={styles.chatHeader}>
        <div className={styles.headerLeft}>
          <img src={BotProfilePic} alt="Bot" className={styles.headerBotImage} />
          <div>
            <div className={styles.botName}>AI Assistant</div>
            <div className={styles.onlineStatus}>Online</div>
          </div>
        </div>
        <button 
          className={styles.minimizeButton}
          onClick={() => setIsExpanded(false)}
        >
          ✕
        </button>
      </div>

      <div className={styles.messagesContainer}>
        {messages.length === 0 && (
          <div className={styles.welcomeMessage}>
            <div className={styles.welcomeIcon}>👋</div>
            <div className={styles.welcomeText}>
              Hi! I'm your AI fitness assistant. How can I help you today?
            </div>
          </div>
        )}
        
        {messages.map((m, i) => (
          <div key={i} className={m.sender === 'user' ? styles.userMessage : styles.botMessage}>
            {m.sender === 'bot' && <img src={BotProfilePic} alt="Bot" className={styles.messageAvatar} />}
            <div className={styles.messageBubble}>{m.text}</div>
            {m.sender === 'user' && <div className={styles.userAvatar}>👤</div>}
          </div>
        ))}
        
        {isBotTyping && (
          <div className={styles.typingIndicator}>
            <img src={BotProfilePic} alt="Bot" className={styles.messageAvatar} />
            <div className={styles.typingBubble}>
              <div className={styles.typingDots}>
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        )}
      </div>

      <div className={styles.inputContainer}>
        <div className={styles.modelSelection}>
          <button
            className={selectedModel === 'deepseek' ? styles.selectedModel : styles.modelButton}
            onClick={() => setSelectedModel('deepseek')}
          >
            DeepSeek
          </button>
          <button
            className={selectedModel === 'gemini' ? styles.selectedModel : styles.modelButton}
            onClick={() => setSelectedModel('gemini')}
          >
            Gemini
          </button>
        </div>
        <div className={styles.inputWrapper}>
          <input 
            value={input} 
            onChange={e => setInput(e.target.value)} 
            className={styles.messageInput}
            placeholder="Ask me anything about fitness..."
            onKeyPress={handleKeyPress}
          />
          <button onClick={sendMessage} className={styles.sendButton}>
            🚀
          </button>
        </div>
      </div>
    </div>
  );
};

export default MinimizedChatbot; 