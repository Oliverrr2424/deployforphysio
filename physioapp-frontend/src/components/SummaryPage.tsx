// Temporary comment to trigger re-compilation
import React, { useState, useEffect } from 'react';
import { Link, useHistory } from 'react-router-dom';
import styles from './SummaryPage.module.css';
import MinimizedChatbot from './MinimizedChatbot';
import muscleDiagramImage from '../assets/muscle_diagram.png'; // Import the image

interface ExerciseSession {
  session_date: string;
  exercise_name: string;
  sets: number;
  reps: number;
  weight?: number;
  notes?: string;
  created_at: string;
}

interface ExerciseLog {
  log_id: string;
  workout_date: string;
  start_time: string;
  end_time: string;
  duration_minutes: number;
  targeted_areas: string;
  notes: string;
  plan_name: string;
}

interface CalendarDay {
  date: string;
  day: number;
  month: number;
  year: number;
  isCurrentMonth: boolean;
  isToday: boolean;
  sessions: ExerciseSession[];
}

const SummaryPage: React.FC = () => {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [calendarDays, setCalendarDays] = useState<CalendarDay[]>([]);
  const [selectedDate, setSelectedDate] = useState<string>('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showAddExercise, setShowAddExercise] = useState(false);
  const [statistics, setStatistics] = useState({
    totalWorkouts: 0,
    thisMonth: 0,
    currentStreak: 0,
    avgPerWeek: 0
  });
  const [newExercise, setNewExercise] = useState({
    exerciseName: '',
    sets: 3,
    reps: 10,
    weight: '',
    notes: ''
  });
  const history = useHistory();

  const generateCalendar = () => {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();
    const firstDay = new Date(year, month, 1);
    const startDate = new Date(firstDay);
    startDate.setDate(startDate.getDate() - firstDay.getDay());
    
    const days: CalendarDay[] = [];
    const today = new Date();
    
    for (let i = 0; i < 42; i++) {
      const date = new Date(startDate);
      date.setDate(startDate.getDate() + i);
      
      days.push({
        date: date.toISOString().split('T')[0],
        day: date.getDate(),
        month: date.getMonth(),
        year: date.getFullYear(),
        isCurrentMonth: date.getMonth() === month,
        isToday: date.toDateString() === today.toDateString(),
        sessions: []
      });
    }
    
    setCalendarDays(days);
  };

  useEffect(() => {
    fetchExerciseLogs();
    generateCalendar();
  }, [currentDate]);

  useEffect(() => {
    if (selectedDate) {
      fetchSessionsForDate(selectedDate);
    }
  }, [selectedDate]);

  const fetchExerciseLogs = async () => {
    try {
      setLoading(true);
      const userDataString = localStorage.getItem('userData');
      const userId = userDataString ? JSON.parse(userDataString).userId : null;
      
      if (!userId) {
        setError('Please log in to view exercise logs');
        setLoading(false);
        return;
      }

      // Get current month date range
      const year = currentDate.getFullYear();
      const month = currentDate.getMonth();
      const startDate = new Date(year, month, 1).toISOString().split('T')[0];
      const endDate = new Date(year, month + 1, 0).toISOString().split('T')[0];
      
      // Fetch sessions for current month
      const response = await fetch(`http://localhost:8080/api/exercise-logs/sessions/calendar?userId=${userId}&startDate=${startDate}&endDate=${endDate}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        const sessions = data.sessions || {};
        
        // Calculate statistics
        const workoutDays = Object.keys(sessions).length;
        const totalExercises = Object.values(sessions).reduce((acc: number, dayExercises: any) => acc + dayExercises.length, 0);
        
        // Calculate streak (simplified - consecutive days with workouts)
        const today = new Date();
        let streak = 0;
        for (let i = 0; i < 30; i++) {
          const checkDate = new Date(today);
          checkDate.setDate(today.getDate() - i);
          const dateStr = checkDate.toISOString().split('T')[0];
          if (sessions[dateStr] && sessions[dateStr].length > 0) {
            streak++;
          } else if (i > 0) {
            break; // Streak broken
          }
        }
        
        // Update statistics
        setStatistics({
          totalWorkouts: totalExercises,
          thisMonth: workoutDays,
          currentStreak: streak,
          avgPerWeek: workoutDays > 0 ? (workoutDays / 4) : 0 // Rough weekly average
        });

        // Update calendar with session data
        setCalendarDays(prev => prev.map(day => {
          const daySessions = sessions[day.date] || [];
          return { ...day, sessions: daySessions };
        }));
        
        setLoading(false);
      } else {
        setError('Failed to fetch exercise logs');
        setLoading(false);
      }
    } catch (err) {
      setError('Error loading exercise logs');
      console.error('Error fetching exercise logs:', err);
      setLoading(false);
    }
  };

  const fetchSessionsForDate = async (date: string) => {
    try {
      const userDataString = localStorage.getItem('userData');
      const userId = userDataString ? JSON.parse(userDataString).userId : null;
      
      if (!userId) return;
      
      const response = await fetch(`http://localhost:8080/api/exercise-logs/sessions/calendar?userId=${userId}&startDate=${date}&endDate=${date}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        const sessions = data.sessions[date] || [];
        
        setCalendarDays(prev => prev.map(day => 
          day.date === date ? { ...day, sessions } : day
        ));
      }
    } catch (err) {
      console.error('Error fetching sessions for date:', err);
    }
  };

  const handleAddExercise = async () => {
    try {
      const userDataString = localStorage.getItem('userData');
      const userId = userDataString ? JSON.parse(userDataString).userId : null;
      
      if (!userId || !selectedDate) return;
      
      const response = await fetch('http://localhost:8080/api/exercise-logs/session/direct', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        },
        body: JSON.stringify({
          userId,
          sessionDate: selectedDate,
          exerciseName: newExercise.exerciseName,
          sets: newExercise.sets,
          reps: newExercise.reps,
          weight: newExercise.weight || null,
          notes: newExercise.notes
        })
      });
      
      if (response.ok) {
        setShowAddExercise(false);
        setNewExercise({
          exerciseName: '',
          sets: 3,
          reps: 10,
          weight: '',
          notes: ''
        });
        fetchSessionsForDate(selectedDate);
      }
    } catch (err) {
      console.error('Error adding exercise:', err);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userData');
    localStorage.removeItem('stepperData');
    history.push('/login');
  };

  const getMonthName = (date: Date) => {
    return date.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
  };

  const selectedDaySessions = calendarDays.find(day => day.date === selectedDate)?.sessions || [];

  return (
    <div className={styles.container}>
      {/* Navigation Header */}
      <header className={styles.header}>
        <div className={styles.headerLeft}>
          <h1 className={styles.logo}>PhysioApp</h1>
        </div>
        <nav className={styles.nav}>
          <Link to="/home" className={styles.navButton}>Home</Link>
          <Link to="/summary" className={styles.navButton}>My Plan</Link>
          <Link to="/calendar-summary" className={`${styles.navButton} ${styles.active}`}>Exercise Log & Progress</Link>
          <Link to="/profile" className={styles.navButton}>Profile</Link>
          <button onClick={handleLogout} className={styles.logoutButton}>Sign Out</button>
        </nav>
      </header>

      <main className={styles.main}>
        <div className={styles.pageTitle}>
          <h2>Exercise Log & Progress</h2>
          <p>Track your workouts, view your progress, and analyze your fitness journey</p>
        </div>

        {/* Progress Statistics */}
        <div className={styles.statisticsSection}>
          <div className={styles.statCard}>
            <div className={styles.statNumber}>{statistics.totalWorkouts}</div>
            <div className={styles.statLabel}>Total Workouts</div>
          </div>
          <div className={styles.statCard}>
            <div className={styles.statNumber}>{statistics.thisMonth}</div>
            <div className={styles.statLabel}>This Month</div>
          </div>
          <div className={styles.statCard}>
            <div className={styles.statNumber}>{statistics.currentStreak}</div>
            <div className={styles.statLabel}>Current Streak</div>
          </div>
          <div className={styles.statCard}>
            <div className={styles.statNumber}>{statistics.avgPerWeek.toFixed(1)}</div>
            <div className={styles.statLabel}>Avg/Week</div>
          </div>
        </div>

        <div className={styles.progressContainer}>
          {/* Calendar Section */}
          <div className={styles.calendarSection}>
            <div className={styles.calendarHeader}>
              <h3>Exercise Calendar</h3>
              <div className={styles.calendarControls}>
                <button 
                  onClick={() => {
                    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() - 1));
                  }}
                  className={styles.calendarButton}
                >
                  ←
                </button>
                <span className={styles.calendarMonth}>{getMonthName(currentDate)}</span>
                <button 
                  onClick={() => {
                    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() + 1));
                  }}
                  className={styles.calendarButton}
                >
                  →
                </button>
              </div>
            </div>

            <div className={styles.calendar}>
              {/* Calendar Header */}
              <div className={styles.calendarWeekHeader}>
                {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map(day => (
                  <div key={day} className={styles.calendarDayHeader}>{day}</div>
                ))}
              </div>

              {/* Calendar Days */}
              <div className={styles.calendarGrid}>
                {calendarDays.map((day, index) => (
                  <div
                    key={index}
                    className={`${styles.calendarDay} ${
                      !day.isCurrentMonth ? styles.otherMonth : ''
                    } ${day.isToday ? styles.today : ''} ${
                      selectedDate === day.date ? styles.selected : ''
                    }`}
                    onClick={() => setSelectedDate(day.date)}
                  >
                    <span className={styles.dayNumber}>{day.day}</span>
                    {day.sessions.length > 0 && (
                      <div className={styles.sessionIndicator}>
                        <span className={styles.sessionCount}>{day.sessions.length}</span>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Selected Date Details */}
          <div className={styles.dateDetails}>
            <div className={styles.dateHeader}>
              <h3>
                {selectedDate ? new Date(selectedDate).toLocaleDateString('en-US', {
                  weekday: 'long',
                  year: 'numeric',
                  month: 'long',
                  day: 'numeric'
                }) : 'Select a date'}
              </h3>
              {selectedDate && (
                <button 
                  onClick={() => setShowAddExercise(true)}
                  className={styles.addExerciseButton}
                >
                  ➕ Add Exercise
                </button>
              )}
            </div>

            {selectedDate && (
              <div className={styles.sessionsList}>
                {selectedDaySessions.length === 0 ? (
                  <div className={styles.emptyDay}>
                    <p>No exercises logged for this date</p>
                    <p>Click "Add Exercise" to log your workout</p>
                  </div>
                ) : (
                  selectedDaySessions.map((session, index) => (
                    <div key={index} className={styles.sessionItem}>
                      <div className={styles.sessionHeader}>
                        <h4>{session.exercise_name}</h4>
                        <span className={styles.sessionTime}>
                          {new Date(session.created_at).toLocaleTimeString()}
                        </span>
                      </div>
                      <div className={styles.sessionDetails}>
                        <span>{session.sets} sets × {session.reps} reps</span>
                        {session.weight && <span>{session.weight} kg</span>}
                      </div>
                      {session.notes && (
                        <p className={styles.sessionNotes}>{session.notes}</p>
                      )}
                    </div>
                  ))
                )}
              </div>
            )}
          </div>
        </div>

        {/* Add Exercise Modal */}
        {showAddExercise && (
          <div className={styles.modalOverlay}>
            <div className={styles.modal}>
              <div className={styles.modalHeader}>
                <h3>Add Exercise for {selectedDate}</h3>
                <button 
                  onClick={() => setShowAddExercise(false)}
                  className={styles.closeButton}
                >
                  ×
                </button>
              </div>
              <div className={styles.modalContent}>
                <div className={styles.formGroup}>
                  <label>Exercise Name:</label>
                  <input
                    type="text"
                    value={newExercise.exerciseName}
                    onChange={(e) => setNewExercise({...newExercise, exerciseName: e.target.value})}
                    placeholder="e.g., Push-ups, Squats"
                  />
                </div>
                <div className={styles.formRow}>
                  <div className={styles.formGroup}>
                    <label>Sets:</label>
                    <input
                      type="number"
                      value={newExercise.sets}
                      onChange={(e) => setNewExercise({...newExercise, sets: parseInt(e.target.value)})}
                      min="1"
                    />
                  </div>
                  <div className={styles.formGroup}>
                    <label>Reps:</label>
                    <input
                      type="number"
                      value={newExercise.reps}
                      onChange={(e) => setNewExercise({...newExercise, reps: parseInt(e.target.value)})}
                      min="1"
                    />
                  </div>
                  <div className={styles.formGroup}>
                    <label>Weight (kg):</label>
                    <input
                      type="number"
                      value={newExercise.weight}
                      onChange={(e) => setNewExercise({...newExercise, weight: e.target.value})}
                      placeholder="Optional"
                    />
                  </div>
                </div>
                <div className={styles.formGroup}>
                  <label>Notes:</label>
                  <textarea
                    value={newExercise.notes}
                    onChange={(e) => setNewExercise({...newExercise, notes: e.target.value})}
                    placeholder="Any additional notes about the exercise..."
                  />
                </div>
              </div>
              <div className={styles.modalActions}>
                <button 
                  onClick={() => setShowAddExercise(false)}
                  className={styles.cancelButton}
                >
                  Cancel
                </button>
                <button 
                  onClick={handleAddExercise}
                  className={styles.saveButton}
                  disabled={!newExercise.exerciseName}
                >
                  Save Exercise
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Muscle Diagram */}
        <div className={styles.muscleDiagramContainer}>
          <h4 className={styles.sectionTitle}>Muscle Groups</h4>
          <div className={styles.diagramWrapper}>
            <img src={muscleDiagramImage} alt="Human Muscle Diagram" className={styles.muscleDiagramImage} />
            <p className={styles.muscleDiagramText}>Track which muscle groups you're working</p>
          </div>
        </div>

        {/* Action Buttons */}
        <div className={styles.actionButtons}>
          <Link to="/summary" className={`${styles.button} ${styles.primaryButton}`}>
            🏃‍♂️ Start Workout
          </Link>
          <button className={`${styles.button} ${styles.secondaryButton}`}>
            📊 View Statistics
          </button>
        </div>
      </main>

      <MinimizedChatbot />
    </div>
  );
};

export default SummaryPage; 