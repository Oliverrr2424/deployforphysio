/**
 * PlanPage Component
 * 
 * This component serves as the main exercise plan management interface for PhysioApp.
 * It allows users to generate personalized exercise plans using AI, view their
 * current plan, and start workout sessions.
 * 
 * Key Features:
 * - AI-powered exercise plan generation based on user preferences
 * - Interactive plan stepper for customizing workout parameters
 * - Real-time plan display with exercise details
 * - Session management and workout initiation
 * - Progress tracking integration
 * 
 * User Flow:
 * 1. User selects workout preferences (duration, muscle groups, focus type)
 * 2. AI generates personalized exercise plan
 * 3. User reviews plan and can start workout session
 * 4. Plan data is stored for progress tracking
 * 
 * @author PhysioApp Team
 * @version 1.0
 * @since 2025-01-01
 */

import React, { useState, useEffect } from 'react';
import { Link, useHistory } from 'react-router-dom';
import styles from './PlanPage.module.css';
import MinimizedChatbot from './MinimizedChatbot';

interface Exercise {
  name: string;
  description: string;
  sets: string;
  reps: string;
  equipment: string;
  difficulty: string;
  instructions: string;
}

interface ExercisePlan {
  title?: string;
  description?: string;
  exercises: Exercise[];
  disclaimer?: string;
  planName?: string;
  duration?: string;
  targetedAreas?: string;
}

interface StepperData {
  fitnessGoal: string;
  equipmentAccess: string;
  injuryAreas: string[];
  fitnessLevel: string;
  timeAvailable: string;
  preferredDays: string[];
  targetedAreas?: string[];
  focusType?: string;
}

const PlanPage: React.FC = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedExercise, setSelectedExercise] = useState<Exercise | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [generatedPlan, setGeneratedPlan] = useState<ExercisePlan | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [stepperData, setStepperData] = useState<StepperData | null>(null);
  const history = useHistory();

  useEffect(() => {
    // Load stepper data from localStorage
    const stepperDataString = localStorage.getItem('stepperData');
    if (stepperDataString) {
      setStepperData(JSON.parse(stepperDataString));
    }

    // Load cached generated plan from localStorage
    const cachedPlanString = localStorage.getItem('generatedPlan');
    if (cachedPlanString) {
      try {
        const cachedPlan = JSON.parse(cachedPlanString);
        setGeneratedPlan(cachedPlan);
      } catch (error) {
        console.error('Failed to load cached plan:', error);
        localStorage.removeItem('generatedPlan');
      }
    }
  }, []);

  /**
   * Handles user logout by clearing all stored data and redirecting to login page.
   * 
   * This function removes all user-related data from localStorage including:
   * - Authentication token
   * - User profile data
   * - Stepper preferences
   * - Generated exercise plans
   * 
   * After clearing data, the user is redirected to the login page.
   */
  const handleLogout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userData');
    localStorage.removeItem('stepperData');
    localStorage.removeItem('generatedPlan');
    history.push('/login');
  };

  /**
   * Opens the exercise detail modal to show comprehensive exercise information.
   * 
   * @param exercise The exercise object containing details to display
   */
  const handleViewExercise = (exercise: Exercise) => {
    setSelectedExercise(exercise);
    setIsModalOpen(true);
  };

  /**
   * Closes the exercise detail modal and clears the selected exercise.
   */
  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedExercise(null);
  };

  /**
   * Generates a personalized exercise plan using AI based on user preferences.
   * 
   * This function sends a request to the backend AI service to generate a
   * customized exercise plan based on the user's stepper preferences and
   * profile data. The generated plan is cached in localStorage for future use.
   * 
   * The function handles:
   * - Validation of user session and stepper data
   * - API communication with the exercise recommendation service
   * - Error handling and user feedback
   * - Plan caching for offline access
   * 
   * @throws Error if user session is invalid or API request fails
   */
  const handleGeneratePlan = async () => {
    if (!stepperData) {
      setError('Please set your preferences first using the stepper.');
      return;
    }

    // Get user data from localStorage
    const userData = localStorage.getItem('userData');
    const authToken = localStorage.getItem('authToken');
    if (!userData || !authToken) {
      setError('Please log in again to generate a plan.');
      return;
    }

    let userId;
    try {
      const user = JSON.parse(userData);
      userId = user.userId;
      if (!userId) {
        setError('Invalid user session. Please log in again.');
        return;
      }
    } catch (err) {
      setError('Invalid user session. Please log in again.');
      return;
    }

    setIsLoading(true);
    setError(null);
    try {
      const response = await fetch('http://localhost:8080/api/recommendations/exercise', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authToken}`
        },
        body: JSON.stringify({ 
          userId: userId,
          duration: stepperData.timeAvailable,
          targetedAreas: stepperData.targetedAreas || [],
          difficulty: stepperData.fitnessLevel,
          equipment: stepperData.equipmentAccess,
          goals: stepperData.fitnessGoal,
          notes: '',
          excludedAreas: stepperData.injuryAreas || [],
          focusType: stepperData.focusType || 'General Fitness'
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to generate plan');
      }

      const data = await response.json();
      if (data.success && data.plan) {
        setGeneratedPlan(data.plan);
        // Cache the generated plan in localStorage
        localStorage.setItem('generatedPlan', JSON.stringify(data.plan));
      } else {
        throw new Error(data.message || 'Failed to generate plan');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * Initiates a workout session with all exercises from the generated plan.
   * 
   * This function prepares the exercise session by storing all exercises
   * from the current plan in localStorage and navigating to the exercise
   * session page. It sets the initial exercise index to 0.
   * 
   * The exercise session page will load all exercises and allow the user
   * to complete them sequentially with proper tracking.
   */
  const handleStartAllExercises = () => {
    if (generatedPlan && generatedPlan.exercises.length > 0) {
      // Navigate to exercise session page with all exercises
      localStorage.setItem('currentSessionExercises', JSON.stringify(generatedPlan.exercises));
      localStorage.setItem('currentExerciseIndex', '0');
      history.push('/exercise-session');
    }
  };

  /**
   * Initiates a workout session with a single selected exercise.
   * 
   * @param exercise The exercise to include in the session
   * @param index The index of the exercise in the plan
   */
  const handleSelectExercise = (exercise: Exercise, index: number) => {
    // Navigate to exercise session page with single exercise
    localStorage.setItem('currentSessionExercises', JSON.stringify([exercise]));
    localStorage.setItem('currentExerciseIndex', '0');
    history.push('/exercise-session');
  };

  /**
   * Navigates to the progress/calendar page to start tracking the plan.
   */
  const handleStartPlan = () => {
    // Navigate to calendar summary to start the plan
    history.push('/calendar-summary');
  };

  /**
   * Navigates to the plan stepper to modify user preferences.
   */
  const handleGoToStepper = () => {
    history.push('/plan-stepper');
  };

  return (
    <div className={styles.container}>
      {/* Navigation Header */}
      <header className={styles.header}>
        <div className={styles.headerLeft}>
          <h1 className={styles.logo}>PhysioApp</h1>
        </div>
        <nav className={styles.nav}>
          <Link to="/home" className={styles.navButton}>Home</Link>
          <Link to="/summary" className={`${styles.navButton} ${styles.active}`}>My Plan</Link>
          <Link to="/calendar-summary" className={styles.navButton}>Exercise Log & Progress</Link>
          <Link to="/profile" className={styles.navButton}>Profile</Link>
          <button onClick={handleLogout} className={styles.logoutButton}>Sign Out</button>
        </nav>
      </header>

      <main className={styles.main}>
        <div className={styles.pageTitle}>
          <h2>My Workout Plan</h2>
          <p>Your personalized fitness journey starts here</p>
        </div>

        {/* User Preferences Display */}
        {stepperData ? (
          <div className={styles.preferencesCard}>
            <div className={styles.preferencesHeader}>
              <h3>Your Preferences</h3>
              <Link to="/plan-stepper" className={styles.modifyButton}>
                ⚙️ Modify
              </Link>
            </div>
            <div className={styles.preferencesGrid}>
              <div className={styles.preferenceItem}>
                <span className={styles.preferenceLabel}>Fitness Goal:</span>
                <span className={styles.preferenceValue}>{stepperData.fitnessGoal || 'Not set'}</span>
              </div>
              <div className={styles.preferenceItem}>
                <span className={styles.preferenceLabel}>Equipment:</span>
                <span className={styles.preferenceValue}>{stepperData.equipmentAccess || 'Not set'}</span>
              </div>
              <div className={styles.preferenceItem}>
                <span className={styles.preferenceLabel}>Fitness Level:</span>
                <span className={styles.preferenceValue}>{stepperData.fitnessLevel || 'Not set'}</span>
              </div>
              <div className={styles.preferenceItem}>
                <span className={styles.preferenceLabel}>Time Available:</span>
                <span className={styles.preferenceValue}>{stepperData.timeAvailable || 'Not set'}</span>
              </div>
              <div className={styles.preferenceItem}>
                <span className={styles.preferenceLabel}>Preferred Days:</span>
                <span className={styles.preferenceValue}>
                  {stepperData.preferredDays && stepperData.preferredDays.length > 0 
                    ? stepperData.preferredDays.join(', ') 
                    : 'Not set'}
                </span>
              </div>
              {stepperData.injuryAreas && stepperData.injuryAreas.length > 0 && (
                <div className={styles.preferenceItem}>
                  <span className={styles.preferenceLabel}>Injury Areas:</span>
                  <span className={styles.preferenceValue}>{stepperData.injuryAreas.join(', ')}</span>
                </div>
              )}
            </div>
          </div>
        ) : (
          <div className={styles.setupCard}>
            <div className={styles.setupContent}>
              <h3>🎯 Set Up Your Preferences</h3>
              <p>To generate a personalized workout plan, we need to know your fitness goals and preferences.</p>
              <button 
                className={`${styles.actionButton} ${styles.primary}`}
                onClick={handleGoToStepper}
              >
                🚀 Start Setup
              </button>
            </div>
          </div>
        )}

        {/* Action Buttons */}
        {stepperData && (
          <div className={styles.actionSection}>
            <button 
              className={`${styles.actionButton} ${styles.primary}`}
              onClick={handleGeneratePlan}
              disabled={isLoading}
            >
              {isLoading ? '🔄 Generating Plan...' : '🤖 Generate AI Plan'}
            </button>
            
            {generatedPlan && (
              <>
                <button 
                  className={`${styles.actionButton} ${styles.startAll}`}
                  onClick={handleStartAllExercises}
                >
                  🏃‍♂️ Start All Exercises
                </button>
                <button 
                  className={`${styles.actionButton} ${styles.success}`}
                  onClick={handleStartPlan}
                >
                  🚀 Start My Plan
                </button>
              </>
            )}
          </div>
        )}

        {error && (
          <div className={styles.errorCard}>
            <div className={styles.errorIcon}>⚠️</div>
            <div>
              <h3>Oops! Something went wrong</h3>
              <p>{error}</p>
            </div>
          </div>
        )}

        {generatedPlan && (
          <div className={styles.planCard}>
            <div className={styles.planHeader}>
              <h3>{generatedPlan.title}</h3>
              <p className={styles.planDescription}>{generatedPlan.description}</p>
            </div>
            
            <div className={styles.exercisesList}>
              {generatedPlan.exercises.map((exercise, index) => (
                <div key={index} className={styles.exerciseCard}>
                  <div className={styles.exerciseHeader}>
                    <h4>{exercise.name}</h4>
                    <span className={styles.difficultyBadge}>{exercise.difficulty}</span>
                  </div>
                  <p className={styles.exerciseDescription}>{exercise.description}</p>
                  <div className={styles.exerciseStats}>
                    <div className={styles.stat}>
                      <span className={styles.statLabel}>Sets</span>
                      <span className={styles.statValue}>{exercise.sets}</span>
                    </div>
                    <div className={styles.stat}>
                      <span className={styles.statLabel}>Reps</span>
                      <span className={styles.statValue}>{exercise.reps}</span>
                    </div>
                    <div className={styles.stat}>
                      <span className={styles.statLabel}>Equipment</span>
                      <span className={styles.statValue}>{exercise.equipment}</span>
                    </div>
                  </div>
                  <div className={styles.exerciseActions}>
                    <button 
                      className={styles.viewButton}
                      onClick={() => handleViewExercise(exercise)}
                    >
                      View Details
                    </button>
                    <button 
                      className={styles.selectButton}
                      onClick={() => handleSelectExercise(exercise, index)}
                    >
                      🎯 Select
                    </button>
                  </div>
                </div>
              ))}
            </div>

            {generatedPlan.disclaimer && (
              <div className={styles.disclaimer}>
                <div className={styles.disclaimerIcon}>ℹ️</div>
                <p>{generatedPlan.disclaimer}</p>
              </div>
            )}
          </div>
        )}

        {/* Exercise View Modal */}
        {isModalOpen && selectedExercise && (
          <div className={styles.modal} onClick={handleCloseModal}>
            <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
              <div className={styles.modalHeader}>
                <h3>{selectedExercise.name}</h3>
                <button 
                  className={styles.closeButton}
                  onClick={handleCloseModal}
                >
                  ✕
                </button>
              </div>
              
              <div className={styles.modalBody}>
                <p className={styles.exerciseDescription}>{selectedExercise.description}</p>
                
                <div className={styles.exerciseDetailsGrid}>
                  <div className={styles.detailItem}>
                    <span className={styles.detailLabel}>Sets:</span>
                    <span className={styles.detailValue}>{selectedExercise.sets}</span>
                  </div>
                  <div className={styles.detailItem}>
                    <span className={styles.detailLabel}>Reps:</span>
                    <span className={styles.detailValue}>{selectedExercise.reps}</span>
                  </div>
                  <div className={styles.detailItem}>
                    <span className={styles.detailLabel}>Equipment:</span>
                    <span className={styles.detailValue}>{selectedExercise.equipment}</span>
                  </div>
                  <div className={styles.detailItem}>
                    <span className={styles.detailLabel}>Difficulty:</span>
                    <span className={styles.detailValue}>{selectedExercise.difficulty}</span>
                  </div>
                </div>

                <div className={styles.instructions}>
                  <h4>Instructions:</h4>
                  <p>{selectedExercise.instructions}</p>
                </div>
              </div>
            </div>
          </div>
        )}
      </main>

      <MinimizedChatbot />
    </div>
  );
};

export default PlanPage; 