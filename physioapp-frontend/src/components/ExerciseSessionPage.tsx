/**
 * ExerciseSessionPage Component
 * 
 * This component manages the active workout session where users perform their exercises.
 * It provides a comprehensive exercise tracking interface with real-time session
 * management, exercise progression, and performance logging.
 * 
 * Key Features:
 * - Real-time exercise session tracking with timers
 * - Set and rep counting for each exercise
 * - Exercise completion and skip functionality
 * - Session pause/resume capability
 * - Automatic session logging to database
 * - Progress tracking and analytics
 * 
 * User Flow:
 * 1. User starts session from PlanPage
 * 2. Exercises are loaded from localStorage
 * 3. User completes exercises one by one
 * 4. Session data is logged to database
 * 5. User is redirected to progress page
 * 
 * @author PhysioApp Team
 * @version 1.0
 * @since 2025-01-01
 */

import React, { useState, useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import styles from './ExerciseSessionPage.module.css';

interface Exercise {
  name: string;
  description: string;
  sets: string;
  reps: string;
  equipment: string;
  difficulty: string;
  instructions: string;
  image?: string; // Optional image path for exercise demonstration
}

interface SetDetails {
  setNumber: number;
  repsCompleted: number;
  weight: number;
  duration: number;
  completed: boolean;
}

interface ExerciseLog {
  exerciseName: string;
  setsCompleted: number;
  totalSets: number;
  reps: string;
  completedAt: string;
  skipped: boolean;
  setDetails: SetDetails[];
}

interface WorkoutSession {
  sessionName: string;
  sessionStartTime: string;
  sessionEndTime: string;
  totalDuration: number;
  exercises: ExerciseLog[];
  completedPercentage: number;
}

const ExerciseSessionPage: React.FC = () => {
  const [exercises, setExercises] = useState<Exercise[]>([]);
  const [currentExerciseIndex, setCurrentExerciseIndex] = useState(0);
  const [currentSets, setCurrentSets] = useState(0);
  const [totalSets, setTotalSets] = useState(0);
  const [sessionStartTime, setSessionStartTime] = useState<Date>(new Date());
  const [exerciseStartTime, setExerciseStartTime] = useState<Date>(new Date());
  const [elapsedTime, setElapsedTime] = useState(0);
  const [exerciseElapsedTime, setExerciseElapsedTime] = useState(0);
  const [isSessionActive, setIsSessionActive] = useState(true);
  const [completedExercises, setCompletedExercises] = useState<ExerciseLog[]>([]);
  const [isPaused, setIsPaused] = useState(false);
  const history = useHistory();

  useEffect(() => {
    // Load exercises from localStorage
    const exercisesString = localStorage.getItem('currentSessionExercises');
    const exerciseIndexString = localStorage.getItem('currentExerciseIndex');
    
    if (exercisesString) {
      const loadedExercises = JSON.parse(exercisesString);
      setExercises(loadedExercises);
      
      if (exerciseIndexString) {
        setCurrentExerciseIndex(parseInt(exerciseIndexString));
      }
      
      // Parse total sets for current exercise
      if (loadedExercises.length > 0) {
        const currentExercise = loadedExercises[parseInt(exerciseIndexString || '0')];
        const sets = parseInt(currentExercise.sets) || 3;
        setTotalSets(sets);
      }
    } else {
      // No exercises found, redirect back
      history.push('/summary');
    }
  }, [history]);

  useEffect(() => {
    let interval: NodeJS.Timeout;
    
    if (isSessionActive && !isPaused) {
      interval = setInterval(() => {
        setElapsedTime(Math.floor((Date.now() - sessionStartTime.getTime()) / 1000));
        setExerciseElapsedTime(Math.floor((Date.now() - exerciseStartTime.getTime()) / 1000));
      }, 1000);
    }
    
    return () => clearInterval(interval);
  }, [isSessionActive, isPaused, sessionStartTime, exerciseStartTime]);

  /**
   * Formats seconds into a readable MM:SS time format.
   * 
   * @param seconds The number of seconds to format
   * @returns Formatted time string in MM:SS format
   */
  const formatTime = (seconds: number): string => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  /**
   * Handles completion of a single set for the current exercise.
   * 
   * This function increments the set counter and automatically moves to the
   * next exercise when all sets for the current exercise are completed.
   * It triggers handleExerciseComplete() when the final set is finished.
   */
  const handleSetComplete = () => {
    if (currentSets < totalSets) {
      setCurrentSets(currentSets + 1);
      
      // If all sets completed, move to next exercise
      if (currentSets + 1 >= totalSets) {
        handleExerciseComplete();
      }
    }
  };

  /**
   * Marks the current exercise as completed and logs the performance data.
   * 
   * This function creates an exercise log entry with completion details including:
   * - Exercise name and completion status
   * - Number of sets completed vs total sets
   * - Reps performed
   * - Timestamp of completion
   * 
   * The exercise is marked as not skipped and added to the completed exercises list.
   */
  const handleExerciseComplete = () => {
    const currentExercise = exercises[currentExerciseIndex];
    const exerciseLog: ExerciseLog = {
      exerciseName: currentExercise.name,
      setsCompleted: currentSets + 1,
      totalSets: totalSets,
      reps: currentExercise.reps,
      completedAt: new Date().toISOString(),
      skipped: false,
      setDetails: []
    };
    
    setCompletedExercises([...completedExercises, exerciseLog]);
    moveToNextExercise();
  };

  /**
   * Marks the current exercise as skipped and logs the skip action.
   * 
   * This function creates an exercise log entry indicating the exercise was skipped.
   * It records the current progress (sets completed before skipping) and moves
   * to the next exercise in the sequence.
   */
  const handleSkipExercise = () => {
    const currentExercise = exercises[currentExerciseIndex];
    const exerciseLog: ExerciseLog = {
      exerciseName: currentExercise.name,
      setsCompleted: currentSets,
      totalSets: totalSets,
      reps: currentExercise.reps,
      completedAt: new Date().toISOString(),
      skipped: true,
      setDetails: []
    };
    
    setCompletedExercises([...completedExercises, exerciseLog]);
    moveToNextExercise();
  };

  /**
   * Advances to the next exercise in the session or completes the session.
   * 
   * This function handles exercise progression by:
   * - Moving to the next exercise index
   * - Resetting set counters for the new exercise
   * - Updating total sets based on the new exercise
   * - Resetting the exercise timer
   * - Updating localStorage with current progress
   * 
   * If all exercises are completed, it triggers session completion.
   */
  const moveToNextExercise = () => {
    if (currentExerciseIndex < exercises.length - 1) {
      const nextIndex = currentExerciseIndex + 1;
      setCurrentExerciseIndex(nextIndex);
      setCurrentSets(0);
      
      // Update total sets for next exercise
      const nextExercise = exercises[nextIndex];
      const sets = parseInt(nextExercise.sets) || 3;
      setTotalSets(sets);
      
      // Reset exercise timer
      setExerciseStartTime(new Date());
      localStorage.setItem('currentExerciseIndex', nextIndex.toString());
    } else {
      // All exercises completed
      handleSessionComplete();
    }
  };

  /**
   * Saves session progress with partial completion status
   */
  const handleSaveAndQuit = async () => {
    if (window.confirm('Save your progress and quit? Your current progress will be saved.')) {
      await saveProgressToDatabase('partial');
      // Clean up localStorage
      localStorage.removeItem('currentSessionExercises');
      localStorage.removeItem('currentExerciseIndex');
      history.push('/summary');
    }
  };

  /**
   * Saves session progress to database in real-time
   */
  const saveProgressToDatabase = async (status: 'completed' | 'partial' | 'skipped') => {
    const userDataString = localStorage.getItem('userData');
    const userId = userDataString ? JSON.parse(userDataString).userId : null;
    
    if (!userId) {
      console.error('No user ID found, cannot save session');
      return false;
    }

    // Add current exercise in progress to completed exercises if there's progress
    let finalExercises = [...completedExercises];
    if (currentSets > 0 && currentExerciseIndex < exercises.length) {
      const currentExercise = exercises[currentExerciseIndex];
      const exerciseLog: ExerciseLog = {
        exerciseName: currentExercise.name,
        setsCompleted: currentSets,
        totalSets: parseInt(currentExercise.sets) || 3,
        reps: currentExercise.reps,
        completedAt: new Date().toISOString(),
        skipped: status === 'skipped',
        setDetails: []
      };
      finalExercises.push(exerciseLog);
    }
    
    const sessionLog = {
      userId: userId,
      exercises: finalExercises,
      sessionStartTime: sessionStartTime.toISOString(),
      sessionEndTime: new Date().toISOString(),
      totalDuration: elapsedTime,
      status: status
    };
    
    try {
      const response = await fetch('http://localhost:8080/api/exercise-logs', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        },
        body: JSON.stringify(sessionLog)
      });
      
      if (response.ok) {
        console.log(`Session logged successfully with status: ${status}`);
        return true;
      } else {
        console.error('Failed to log session:', response.status, response.statusText);
        return false;
      }
    } catch (error) {
      console.error('Failed to log session:', error);
      return false;
    }
  };

  /**
   * Handles skipping the entire workout session with user confirmation.
   * 
   * This function prompts the user for confirmation before skipping the entire
   * session. If confirmed, it immediately completes the session without logging
   * individual exercise data.
   */
  const handleSkipSession = () => {
    if (window.confirm('Are you sure you want to skip the entire session?')) {
      handleSessionComplete();
    }
  };

  /**
   * Completes the workout session and logs all data to the database.
   * 
   * This function performs the final session cleanup and data persistence:
   * - Stops session timers and tracking
   * - Saves session data with 'completed' status
   * - Cleans up localStorage session data
   * - Redirects user to progress/calendar page
   */
  const handleSessionComplete = async () => {
    setIsSessionActive(false);
    
    const success = await saveProgressToDatabase('completed');
    
    if (success) {
      // Clean up localStorage
      localStorage.removeItem('currentSessionExercises');
      localStorage.removeItem('currentExerciseIndex');
      
      // Navigate to progress page
      history.push('/summary');
    } else {
      // Still clean up and navigate even if save failed
      localStorage.removeItem('currentSessionExercises');
      localStorage.removeItem('currentExerciseIndex');
      history.push('/summary');
    }
  };

  /**
   * Toggles session pause/resume functionality.
   * 
   * This function handles session pausing and resuming by:
   * - Toggling the pause state
   * - Adjusting timers to account for pause duration
   * - Maintaining accurate session and exercise timing
   * 
   * When resuming, it calculates the pause duration and adjusts the start times
   * to maintain accurate elapsed time tracking.
   */
  const togglePause = () => {
    setIsPaused(!isPaused);
    if (isPaused) {
      // Resume - update start times to account for pause duration
      const pauseDuration = Date.now() - exerciseStartTime.getTime() - (exerciseElapsedTime * 1000);
      setSessionStartTime(new Date(sessionStartTime.getTime() + pauseDuration));
      setExerciseStartTime(new Date(exerciseStartTime.getTime() + pauseDuration));
    }
  };

  if (exercises.length === 0) {
    return <div>Loading...</div>;
  }

  const currentExercise = exercises[currentExerciseIndex];
  const progress = ((currentExerciseIndex + (currentSets / totalSets)) / exercises.length) * 100;

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.sessionInfo}>
          <h1>Exercise Session</h1>
          <div className={styles.timers}>
            <div className={styles.timer}>
              <span className={styles.timerLabel}>Total Time</span>
              <span className={styles.timerValue}>{formatTime(elapsedTime)}</span>
            </div>
            <div className={styles.timer}>
              <span className={styles.timerLabel}>Exercise Time</span>
              <span className={styles.timerValue}>{formatTime(exerciseElapsedTime)}</span>
            </div>
          </div>
        </div>
        
        <div className={styles.sessionControls}>
          <button 
            className={styles.pauseButton}
            onClick={togglePause}
          >
            {isPaused ? '▶️ Resume' : '⏸️ Pause'}
          </button>
          <button 
            className={styles.saveAndQuitButton}
            onClick={handleSaveAndQuit}
          >
            💾 Save Progress & Quit
          </button>
          <button 
            className={styles.completeSessionButton}
            onClick={handleSessionComplete}
          >
            ✅ Complete Session
          </button>
        </div>
      </div>

      <div className={styles.progressSection}>
        <div className={styles.progressBar}>
          <div 
            className={styles.progressFill}
            style={{ width: `${progress}%` }}
          />
        </div>
        <span className={styles.progressText}>
          Exercise {currentExerciseIndex + 1} of {exercises.length}
        </span>
      </div>

      <div className={styles.exerciseCard}>
        <div className={styles.exerciseHeader}>
          <h2>{currentExercise.name}</h2>
          <span className={styles.difficultyBadge}>{currentExercise.difficulty}</span>
        </div>
        
        {/* Exercise Image Section */}
        <div className={styles.exerciseImageSection}>
          {currentExercise.image ? (
            <img 
              src={`/exercise-images/${currentExercise.image}`} 
              alt={`${currentExercise.name} demonstration`}
              className={styles.exerciseImage}
            />
          ) : (
            <div className={styles.exerciseImagePlaceholder}>
              <span className={styles.placeholderIcon}>🏃‍♂️</span>
              <p>Exercise demonstration image</p>
              <small>Images can be added to /public/exercise-images/ folder</small>
            </div>
          )}
        </div>
        
        <p className={styles.exerciseDescription}>{currentExercise.description}</p>
        
        <div className={styles.exerciseStats}>
          <div className={styles.stat}>
            <span className={styles.statLabel}>Target Sets</span>
            <span className={styles.statValue}>{currentExercise.sets}</span>
          </div>
          <div className={styles.stat}>
            <span className={styles.statLabel}>Target Reps</span>
            <span className={styles.statValue}>{currentExercise.reps}</span>
          </div>
          <div className={styles.stat}>
            <span className={styles.statLabel}>Equipment</span>
            <span className={styles.statValue}>{currentExercise.equipment}</span>
          </div>
        </div>

        <div className={styles.instructions}>
          <h3>Instructions:</h3>
          <p>{currentExercise.instructions}</p>
        </div>
      </div>

      <div className={styles.setsTracker}>
        <h3>Sets Progress</h3>
        <div className={styles.setsGrid}>
          {Array.from({ length: totalSets }, (_, index) => (
            <button
              key={index}
              className={`${styles.setButton} ${
                index < currentSets ? styles.completed : ''
              }`}
              onClick={() => {
                if (index === currentSets) {
                  handleSetComplete();
                }
              }}
              disabled={index > currentSets}
            >
              Set {index + 1}
              {index < currentSets && <span className={styles.checkmark}>✓</span>}
            </button>
          ))}
        </div>
        <div className={styles.setsInfo}>
          <span>{currentSets} of {totalSets} sets completed</span>
        </div>
      </div>

      <div className={styles.actionButtons}>
        <button 
          className={styles.skipExerciseButton}
          onClick={handleSkipExercise}
        >
          ⏭️ Skip Exercise
        </button>
        
        {currentSets >= totalSets && (
          <button 
            className={styles.nextExerciseButton}
            onClick={moveToNextExercise}
          >
            {currentExerciseIndex < exercises.length - 1 ? '➡️ Next Exercise' : '🏁 Finish Session'}
          </button>
        )}
      </div>
    </div>
  );
};

export default ExerciseSessionPage; 