import React, { useState, useEffect } from 'react';
import { Link, useHistory } from 'react-router-dom';
import styles from './PlanStepperPage.module.css';
import MinimizedChatbot from './MinimizedChatbot';

const PlanStepperPage: React.FC = () => {
  const [currentStep, setCurrentStep] = useState(1);
  const [formData, setFormData] = useState({
    fitnessGoal: '',
    equipmentAccess: '',
    injuryAreas: [] as string[],
    fitnessLevel: '',
    timeAvailable: '',
  });
  const history = useHistory();

  useEffect(() => {
    // Load existing stepper data if available
    const stepperDataString = localStorage.getItem('stepperData');
    if (stepperDataString) {
      const existingData = JSON.parse(stepperDataString);
      setFormData(existingData);
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userData');
    localStorage.removeItem('stepperData');
    history.push('/login');
  };

  const handleInputChange = (field: string, value: string | string[]) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleInjuryToggle = (injury: string) => {
    setFormData(prev => ({
      ...prev,
      injuryAreas: prev.injuryAreas.includes(injury)
        ? prev.injuryAreas.filter(i => i !== injury)
        : [...prev.injuryAreas, injury]
    }));
  };

  const handleGeneratePlan = async () => {
    // Store the form data in localStorage for the plan page to use
    localStorage.setItem('stepperData', JSON.stringify(formData));
    
    // Also save to backend if user is authenticated
    const authToken = localStorage.getItem('authToken');
    if (authToken) {
      try {
        await fetch('http://localhost:8080/api/user/session', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${authToken}`,
          },
          body: JSON.stringify({
            stepperData: formData
          }),
        });
      } catch (error) {
        console.error('Failed to save session data:', error);
        // Continue anyway - localStorage will be used as fallback
      }
    }
  };

  const nextStep = () => {
    if (currentStep < 5) {
      setCurrentStep(currentStep + 1);
    }
  };

  const prevStep = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1);
    }
  };

  const renderStep = () => {
    switch (currentStep) {
      case 1:
        return (
          <div className={styles.stepContent}>
            <h3>What's your primary fitness goal?</h3>
            <div className={styles.optionGrid}>
              {['Weight Loss', 'Muscle Gain', 'Endurance', 'Strength', 'Flexibility', 'Rehabilitation'].map(goal => (
                <button
                  key={goal}
                  className={`${styles.optionButton} ${formData.fitnessGoal === goal ? styles.selected : ''}`}
                  onClick={() => handleInputChange('fitnessGoal', goal)}
                >
                  {goal}
                </button>
              ))}
            </div>
          </div>
        );
      case 2:
        return (
          <div className={styles.stepContent}>
            <h3>What equipment do you have access to?</h3>
            <div className={styles.optionGrid}>
              {['Full Gym', 'Home Equipment', 'Minimal Equipment', 'Bodyweight Only'].map(equipment => (
                <button
                  key={equipment}
                  className={`${styles.optionButton} ${formData.equipmentAccess === equipment ? styles.selected : ''}`}
                  onClick={() => handleInputChange('equipmentAccess', equipment)}
                >
                  {equipment}
                </button>
              ))}
            </div>
          </div>
        );
      case 3:
        return (
          <div className={styles.stepContent}>
            <h3>Do you have any injury areas to focus on?</h3>
            <div className={styles.optionGrid}>
              {['Lower Back', 'Knee', 'Shoulder', 'Ankle', 'Neck', 'Hip', 'Wrist', 'None'].map(injury => (
                <button
                  key={injury}
                  className={`${styles.optionButton} ${formData.injuryAreas.includes(injury) ? styles.selected : ''}`}
                  onClick={() => handleInjuryToggle(injury)}
                >
                  {injury}
                </button>
              ))}
            </div>
          </div>
        );
      case 4:
        return (
          <div className={styles.stepContent}>
            <h3>What's your current fitness level?</h3>
            <div className={styles.optionGrid}>
              {['Beginner', 'Intermediate', 'Advanced'].map(level => (
                <button
                  key={level}
                  className={`${styles.optionButton} ${formData.fitnessLevel === level ? styles.selected : ''}`}
                  onClick={() => handleInputChange('fitnessLevel', level)}
                >
                  {level}
                </button>
              ))}
            </div>
          </div>
        );
      case 5:
        return (
          <div className={styles.stepContent}>
            <h3>How much time can you dedicate per session?</h3>
            <div className={styles.optionGrid}>
              {['15-30 minutes', '30-45 minutes', '45-60 minutes', '60+ minutes'].map(time => (
                <button
                  key={time}
                  className={`${styles.optionButton} ${formData.timeAvailable === time ? styles.selected : ''}`}
                  onClick={() => handleInputChange('timeAvailable', time)}
                >
                  {time}
                </button>
              ))}
            </div>
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className={styles.container}>
      {/* Navigation Header */}
      <header className={styles.header}>
        <div className={styles.headerLeft}>
          <Link to="/summary" className={styles.backButton}>
            ← Back to Plan
          </Link>
          <h1 className={styles.logo}>PhysioApp</h1>
        </div>
        <nav className={styles.nav}>
          <Link to="/home" className={styles.navButton}>Home</Link>
          <Link to="/summary" className={styles.navButton}>My Plan</Link>
          <Link to="/calendar-summary" className={styles.navButton}>Exercise Log & Progress</Link>
          <Link to="/profile" className={styles.navButton}>Profile</Link>
          <button onClick={handleLogout} className={styles.logoutButton}>Sign Out</button>
        </nav>
      </header>

      <main className={styles.main}>
        <div className={styles.card}>
          <div className={styles.cardHeader}>
            <h2 className={styles.title}>Personalize Your Fitness Plan</h2>
            <p className={styles.subtitle}>Let's customize your workout experience</p>
          </div>
          
          {/* Progress Bar */}
          <div className={styles.progressContainer}>
            <div className={styles.progressBar}>
              <div 
                className={styles.progressFill}
                style={{ width: `${(currentStep / 5) * 100}%` }}
              />
            </div>
            <span className={styles.progressText}>Step {currentStep} of 5</span>
          </div>
          
          {/* Step Content */}
          {renderStep()}
          
          {/* Navigation Buttons */}
          <div className={styles.buttonGroup}>
            <button
              className={`${styles.button} ${styles.secondaryButton}`}
              onClick={prevStep}
              disabled={currentStep === 1}
            >
              ← Previous
            </button>
            {currentStep === 5 ? (
              <Link
                to="/summary"
                className={`${styles.button} ${styles.primaryButton}`}
                onClick={handleGeneratePlan}
              >
                Save Preferences →
              </Link>
            ) : (
              <button
                className={`${styles.button} ${styles.primaryButton}`}
                onClick={nextStep}
              >
                Next →
              </button>
            )}
          </div>
        </div>
      </main>

      <MinimizedChatbot />
    </div>
  );
};

export default PlanStepperPage; 