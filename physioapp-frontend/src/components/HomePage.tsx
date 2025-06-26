import React from 'react';
import { Link, useHistory } from 'react-router-dom';
import styles from './HomePage.module.css';
import MinimizedChatbot from './MinimizedChatbot';

const HomePage: React.FC = () => {
  const history = useHistory();

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userData');
    localStorage.removeItem('stepperData');
    history.push('/login');
  };

  const userData = JSON.parse(localStorage.getItem('userData') || '{}');

  return (
    <div className={styles.container}>
      {/* Navigation Header */}
      <header className={styles.header}>
        <div className={styles.headerLeft}>
          <h1 className={styles.logo}>PhysioApp</h1>
        </div>
        <nav className={styles.nav}>
          <Link to="/home" className={`${styles.navButton} ${styles.active}`}>Home</Link>
          <Link to="/summary" className={styles.navButton}>My Plan</Link>
          <Link to="/calendar-summary" className={styles.navButton}>Exercise Log & Progress</Link>
          <Link to="/profile" className={styles.navButton}>Profile</Link>
          <button onClick={handleLogout} className={styles.logoutButton}>Sign Out</button>
        </nav>
      </header>

      {/* Main Content */}
      <main className={styles.main}>
        <div className={styles.welcomeSection}>
          <h2 className={styles.welcomeTitle}>Welcome back, {userData.name || userData.username}!</h2>
          <p className={styles.welcomeSubtitle}>Ready to continue your fitness journey?</p>
        </div>

        <div className={styles.dashboardGrid}>
          {/* Quick Actions Card */}
          <div className={styles.card}>
            <h3 className={styles.cardTitle}>Quick Actions</h3>
            <div className={styles.actionButtons}>
              <Link to="/summary" className={`${styles.actionButton} ${styles.primary}`}>
                <div className={styles.actionIcon}>🏃‍♂️</div>
                <div>
                  <div className={styles.actionTitle}>Start My Plan</div>
                  <div className={styles.actionSubtitle}>Begin your workout routine</div>
                </div>
              </Link>
              <Link to="/plan-stepper" className={`${styles.actionButton} ${styles.secondary}`}>
                <div className={styles.actionIcon}>⚙️</div>
                <div>
                  <div className={styles.actionTitle}>Update Preferences</div>
                  <div className={styles.actionSubtitle}>Modify your fitness goals</div>
                </div>
              </Link>
              <Link to="/profile" className={`${styles.actionButton} ${styles.secondary}`}>
                <div className={styles.actionIcon}>👤</div>
                <div>
                  <div className={styles.actionTitle}>Edit Profile</div>
                  <div className={styles.actionSubtitle}>Update your information</div>
                </div>
              </Link>
            </div>
          </div>

          {/* Today's Overview Card */}
          <div className={styles.card}>
            <h3 className={styles.cardTitle}>Today's Overview</h3>
            <div className={styles.overviewStats}>
              <div className={styles.stat}>
                <div className={styles.statNumber}>0</div>
                <div className={styles.statLabel}>Exercises Completed</div>
              </div>
              <div className={styles.stat}>
                <div className={styles.statNumber}>0</div>
                <div className={styles.statLabel}>Minutes Active</div>
              </div>
              <div className={styles.stat}>
                <div className={styles.statNumber}>0</div>
                <div className={styles.statLabel}>Calories Burned</div>
              </div>
            </div>
          </div>

          {/* Recent Activity Card */}
          <div className={styles.card}>
            <h3 className={styles.cardTitle}>Recent Activity</h3>
            <div className={styles.activityList}>
              <div className={styles.activityItem}>
                <div className={styles.activityIcon}>🎯</div>
                <div className={styles.activityContent}>
                  <div className={styles.activityTitle}>Profile Created</div>
                  <div className={styles.activityTime}>Welcome to PhysioApp!</div>
                </div>
              </div>
            </div>
          </div>

          {/* Motivation Card */}
          <div className={styles.card}>
            <h3 className={styles.cardTitle}>Daily Motivation</h3>
            <div className={styles.motivationContent}>
              <div className={styles.motivationQuote}>
                "The only bad workout is the one that didn't happen."
              </div>
              <div className={styles.motivationAuthor}>- Unknown</div>
            </div>
          </div>
        </div>
      </main>

      <MinimizedChatbot />
    </div>
  );
};

export default HomePage; 