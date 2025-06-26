import React, { useState, useEffect } from 'react';
import { useHistory, Link } from 'react-router-dom';
import styles from './ProfilePage.module.css';
import MinimizedChatbot from './MinimizedChatbot';
import Chatbot from './Chatbot';

interface UserProfile {
  userId: string;
  username: string;
  email: string;
  name: string;
  gender: string;
  age: number;
  phone: string;
  height: number;
  weight: number;
  chronicDiseases: string;
  injuryHistory: string;
  fitnessGoal: string;
  equipmentAccess: string;
  role: string;
}

const ProfilePage: React.FC = () => {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [editForm, setEditForm] = useState<Partial<UserProfile>>({});
  const [isSaving, setIsSaving] = useState(false);
  const history = useHistory();

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    const authToken = localStorage.getItem('authToken');
    if (!authToken) {
      history.push('/login');
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/user/profile', {
        headers: {
          'Authorization': `Bearer ${authToken}`,
        },
      });

      const data = await response.json();
      if (data.success) {
        setProfile(data.user);
        setEditForm(data.user);
      } else {
        setError(data.message);
      }
    } catch (err) {
      setError('Failed to load profile');
    } finally {
      setIsLoading(false);
    }
  };

  const handleEditSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaving(true);
    const authToken = localStorage.getItem('authToken');

    try {
      const response = await fetch('http://localhost:8080/api/user/profile', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authToken}`,
        },
        body: JSON.stringify(editForm),
      });

      const data = await response.json();
      if (data.success) {
        setProfile({ ...profile, ...editForm } as UserProfile);
        setIsEditing(false);
        setError('');
      } else {
        setError(data.message);
      }
    } catch (err) {
      setError('Failed to update profile');
    } finally {
      setIsSaving(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userData');
    localStorage.removeItem('stepperData');
    history.push('/login');
  };

  const isFieldEmpty = (value: any) => {
    return !value || value === '' || value === 0;
  };

  const getMissingFields = () => {
    if (!profile) return [];
    const requiredFields = [
      { key: 'name', label: 'Name' },
      { key: 'gender', label: 'Gender' },
      { key: 'age', label: 'Age' },
      { key: 'height', label: 'Height' },
      { key: 'weight', label: 'Weight' },
      { key: 'phone', label: 'Phone' },
      { key: 'fitnessGoal', label: 'Fitness Goal' },
      { key: 'equipmentAccess', label: 'Equipment Access' }
    ];
    
    return requiredFields.filter(field => isFieldEmpty(profile[field.key as keyof UserProfile]));
  };

  if (isLoading) {
    return (
      <div className={styles.container}>
        <div className={styles.loadingContainer}>
          <div className={styles.spinner}></div>
          <p className={styles.loadingText}>Loading your profile...</p>
        </div>
        <Chatbot />
      </div>
    );
  }

  if (!profile) {
    return (
      <div className={styles.container}>
        <div className={styles.errorContainer}>
          <div className={styles.errorIcon}>⚠️</div>
          <h3>Unable to load profile</h3>
          <p>Please try refreshing the page</p>
        </div>
        <Chatbot />
      </div>
    );
  }

  const missingFields = getMissingFields();

  return (
    <div className={styles.container}>
      {/* Navigation Header */}
      <header className={styles.navHeader}>
        <div className={styles.navLeft}>
          <h1 className={styles.logo}>PhysioApp</h1>
        </div>
        <nav className={styles.nav}>
          <Link to="/home" className={styles.navButton}>Home</Link>
          <Link to="/summary" className={styles.navButton}>My Plan</Link>
          <Link to="/calendar-summary" className={styles.navButton}>Progress</Link>
          <Link to="/profile" className={`${styles.navButton} ${styles.active}`}>Profile</Link>
          <button onClick={handleLogout} className={styles.navLogoutButton}>Sign Out</button>
        </nav>
      </header>

      <main className={styles.main}>
        <div className={styles.profileCard}>
        {/* Header */}
        <div className={styles.header}>
          <div className={styles.headerLeft}>
            <div className={styles.avatar}>
              {profile.name ? profile.name.charAt(0).toUpperCase() : profile.username.charAt(0).toUpperCase()}
            </div>
            <div className={styles.headerInfo}>
              <h1 className={styles.userName}>{profile.name || profile.username}</h1>
              <p className={styles.userEmail}>{profile.email}</p>
            </div>
          </div>
          <div className={styles.headerActions}>
            <button 
              onClick={() => setIsEditing(!isEditing)} 
              className={`${styles.button} ${isEditing ? styles.secondaryButton : styles.primaryButton}`}
              disabled={isSaving}
            >
              {isEditing ? 'Cancel' : 'Edit Profile'}
            </button>
            <button onClick={handleLogout} className={`${styles.button} ${styles.logoutButton}`}>
              Sign Out
            </button>
          </div>
        </div>

        {/* Missing Fields Alert */}
        {!isEditing && missingFields.length > 0 && (
          <div className={styles.missingFieldsAlert}>
            <div className={styles.alertIcon}>ℹ️</div>
            <div className={styles.alertContent}>
              <h3>Complete your profile</h3>
              <p>Add {missingFields.map(f => f.label).join(', ')} to get personalized recommendations</p>
              <button 
                onClick={() => setIsEditing(true)} 
                className={`${styles.button} ${styles.alertButton}`}
              >
                Complete Profile
              </button>
            </div>
          </div>
        )}

        {/* Error Message */}
        {error && (
          <div className={styles.errorMessage}>
            <span className={styles.errorIcon}>⚠️</span>
            {error}
          </div>
        )}

        {/* Content */}
        {isEditing ? (
          <form onSubmit={handleEditSubmit} className={styles.editForm}>
            <div className={styles.formSection}>
              <h2 className={styles.sectionTitle}>Personal Information</h2>
              <div className={styles.formGrid}>
                <div className={styles.inputGroup}>
                  <label className={styles.label}>Full Name</label>
                  <input
                    type="text"
                    value={editForm.name || ''}
                    onChange={(e) => setEditForm({ ...editForm, name: e.target.value })}
                    className={styles.input}
                    placeholder="Enter your full name"
                  />
                </div>
                <div className={styles.inputGroup}>
                  <label className={styles.label}>Email</label>
                  <input
                    type="email"
                    value={editForm.email || ''}
                    onChange={(e) => setEditForm({ ...editForm, email: e.target.value })}
                    className={styles.input}
                    placeholder="Enter your email"
                  />
                </div>
                <div className={styles.inputGroup}>
                  <label className={styles.label}>Gender</label>
                  <select
                    value={editForm.gender || ''}
                    onChange={(e) => setEditForm({ ...editForm, gender: e.target.value })}
                    className={styles.select}
                  >
                    <option value="">Select Gender</option>
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                    <option value="Other">Other</option>
                    <option value="Prefer not to say">Prefer not to say</option>
                  </select>
                </div>
                <div className={styles.inputGroup}>
                  <label className={styles.label}>Age</label>
                  <input
                    type="number"
                    value={editForm.age || ''}
                    onChange={(e) => setEditForm({ ...editForm, age: parseInt(e.target.value) || 0 })}
                    className={styles.input}
                    placeholder="Enter your age"
                    min="1"
                    max="120"
                  />
                </div>
                <div className={styles.inputGroup}>
                  <label className={styles.label}>Phone Number</label>
                  <input
                    type="tel"
                    value={editForm.phone || ''}
                    onChange={(e) => setEditForm({ ...editForm, phone: e.target.value })}
                    className={styles.input}
                    placeholder="Enter your phone number"
                  />
                </div>
              </div>
            </div>

            <div className={styles.formSection}>
              <h2 className={styles.sectionTitle}>Physical Information</h2>
              <div className={styles.formGrid}>
                <div className={styles.inputGroup}>
                  <label className={styles.label}>Height (cm)</label>
                  <input
                    type="number"
                    value={editForm.height || ''}
                    onChange={(e) => setEditForm({ ...editForm, height: parseFloat(e.target.value) || 0 })}
                    className={styles.input}
                    placeholder="Enter your height"
                    min="50"
                    max="300"
                  />
                </div>
                <div className={styles.inputGroup}>
                  <label className={styles.label}>Weight (kg)</label>
                  <input
                    type="number"
                    value={editForm.weight || ''}
                    onChange={(e) => setEditForm({ ...editForm, weight: parseFloat(e.target.value) || 0 })}
                    className={styles.input}
                    placeholder="Enter your weight"
                    min="20"
                    max="500"
                    step="0.1"
                  />
                </div>
              </div>
            </div>

            <div className={styles.formSection}>
              <h2 className={styles.sectionTitle}>Fitness Information</h2>
              <div className={styles.inputGroup}>
                <label className={styles.label}>Fitness Goal</label>
                <select
                  value={editForm.fitnessGoal || ''}
                  onChange={(e) => setEditForm({ ...editForm, fitnessGoal: e.target.value })}
                  className={styles.select}
                >
                  <option value="">Select your fitness goal</option>
                  <option value="Weight Loss">Weight Loss</option>
                  <option value="Muscle Gain">Muscle Gain</option>
                  <option value="Endurance">Endurance</option>
                  <option value="Strength">Strength</option>
                  <option value="General Fitness">General Fitness</option>
                  <option value="Rehabilitation">Rehabilitation</option>
                </select>
              </div>
              <div className={styles.inputGroup}>
                <label className={styles.label}>Equipment Access</label>
                <select
                  value={editForm.equipmentAccess || ''}
                  onChange={(e) => setEditForm({ ...editForm, equipmentAccess: e.target.value })}
                  className={styles.select}
                >
                  <option value="">Select equipment access</option>
                  <option value="Full Gym">Full Gym</option>
                  <option value="Home Gym">Home Gym</option>
                  <option value="Basic Equipment">Basic Equipment (dumbbells, resistance bands)</option>
                  <option value="No Equipment">No Equipment (bodyweight only)</option>
                </select>
              </div>
            </div>

            <div className={styles.formSection}>
              <h2 className={styles.sectionTitle}>Health Information</h2>
              <div className={styles.inputGroup}>
                <label className={styles.label}>Chronic Diseases</label>
                <textarea
                  value={editForm.chronicDiseases || ''}
                  onChange={(e) => setEditForm({ ...editForm, chronicDiseases: e.target.value })}
                  className={styles.textarea}
                  rows={3}
                  placeholder="List any chronic diseases or conditions (optional)"
                />
              </div>
              <div className={styles.inputGroup}>
                <label className={styles.label}>Injury History</label>
                <textarea
                  value={editForm.injuryHistory || ''}
                  onChange={(e) => setEditForm({ ...editForm, injuryHistory: e.target.value })}
                  className={styles.textarea}
                  rows={3}
                  placeholder="Describe any past injuries or current limitations (optional)"
                />
              </div>
            </div>

            <div className={styles.formActions}>
              <button 
                type="button" 
                onClick={() => setIsEditing(false)} 
                className={`${styles.button} ${styles.secondaryButton}`}
                disabled={isSaving}
              >
                Cancel
              </button>
              <button 
                type="submit" 
                className={`${styles.button} ${styles.primaryButton}`}
                disabled={isSaving}
              >
                {isSaving ? 'Saving...' : 'Save Changes'}
              </button>
            </div>
          </form>
        ) : (
          <div className={styles.profileDisplay}>
            <div className={styles.infoSection}>
              <h2 className={styles.sectionTitle}>Personal Information</h2>
              <div className={styles.infoGrid}>
                <div className={`${styles.infoItem} ${isFieldEmpty(profile.name) ? styles.missing : ''}`}>
                  <span className={styles.infoLabel}>Full Name</span>
                  <span className={styles.infoValue}>{profile.name || 'Not provided'}</span>
                </div>
                <div className={styles.infoItem}>
                  <span className={styles.infoLabel}>Username</span>
                  <span className={styles.infoValue}>{profile.username}</span>
                </div>
                <div className={styles.infoItem}>
                  <span className={styles.infoLabel}>Email</span>
                  <span className={styles.infoValue}>{profile.email}</span>
                </div>
                <div className={`${styles.infoItem} ${isFieldEmpty(profile.gender) ? styles.missing : ''}`}>
                  <span className={styles.infoLabel}>Gender</span>
                  <span className={styles.infoValue}>{profile.gender || 'Not provided'}</span>
                </div>
                <div className={`${styles.infoItem} ${isFieldEmpty(profile.age) ? styles.missing : ''}`}>
                  <span className={styles.infoLabel}>Age</span>
                  <span className={styles.infoValue}>{profile.age || 'Not provided'}</span>
                </div>
                <div className={`${styles.infoItem} ${isFieldEmpty(profile.phone) ? styles.missing : ''}`}>
                  <span className={styles.infoLabel}>Phone</span>
                  <span className={styles.infoValue}>{profile.phone || 'Not provided'}</span>
                </div>
              </div>
            </div>

            <div className={styles.infoSection}>
              <h2 className={styles.sectionTitle}>Physical Information</h2>
              <div className={styles.infoGrid}>
                <div className={`${styles.infoItem} ${isFieldEmpty(profile.height) ? styles.missing : ''}`}>
                  <span className={styles.infoLabel}>Height</span>
                  <span className={styles.infoValue}>{profile.height ? `${profile.height} cm` : 'Not provided'}</span>
                </div>
                <div className={`${styles.infoItem} ${isFieldEmpty(profile.weight) ? styles.missing : ''}`}>
                  <span className={styles.infoLabel}>Weight</span>
                  <span className={styles.infoValue}>{profile.weight ? `${profile.weight} kg` : 'Not provided'}</span>
                </div>
                {profile.height && profile.weight && (
                  <div className={styles.infoItem}>
                    <span className={styles.infoLabel}>BMI</span>
                    <span className={styles.infoValue}>
                      {((profile.weight / Math.pow(profile.height / 100, 2)).toFixed(1))}
                    </span>
                  </div>
                )}
              </div>
            </div>

            <div className={styles.infoSection}>
              <h2 className={styles.sectionTitle}>Fitness Information</h2>
              <div className={styles.infoGrid}>
                <div className={`${styles.infoItem} ${isFieldEmpty(profile.fitnessGoal) ? styles.missing : ''}`}>
                  <span className={styles.infoLabel}>Fitness Goal</span>
                  <span className={styles.infoValue}>{profile.fitnessGoal || 'Not provided'}</span>
                </div>
                <div className={`${styles.infoItem} ${isFieldEmpty(profile.equipmentAccess) ? styles.missing : ''}`}>
                  <span className={styles.infoLabel}>Equipment Access</span>
                  <span className={styles.infoValue}>{profile.equipmentAccess || 'Not provided'}</span>
                </div>
              </div>
            </div>

            {(profile.chronicDiseases || profile.injuryHistory) && (
              <div className={styles.infoSection}>
                <h2 className={styles.sectionTitle}>Health Information</h2>
                {profile.chronicDiseases && (
                  <div className={styles.healthInfo}>
                    <h3 className={styles.healthLabel}>Chronic Diseases</h3>
                    <p className={styles.healthValue}>{profile.chronicDiseases}</p>
                  </div>
                )}
                {profile.injuryHistory && (
                  <div className={styles.healthInfo}>
                    <h3 className={styles.healthLabel}>Injury History</h3>
                    <p className={styles.healthValue}>{profile.injuryHistory}</p>
                  </div>
                )}
              </div>
            )}
          </div>
        )}
        </div>
      </main>
      <MinimizedChatbot />
    </div>
  );
};

export default ProfilePage; 