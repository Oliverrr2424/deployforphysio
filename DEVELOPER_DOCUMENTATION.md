# PhysioApp Developer Documentation

## 🚀 Quick Start

**For peers setting up the project for the first time:**

```bash
# 1. Clone the repository
git clone <repository-url>
cd project-9-physioapp

# 2. Run the automated setup script
chmod +x setup-dev.sh
./setup-dev.sh

# 3. Start the backend (Terminal 1)
cd backend
./mvnw spring-boot:run

# 4. Start the frontend (Terminal 2)  
cd physioapp-frontend
npm start
```

**Verify everything works:**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Database: postgresql://postgres:postgres@localhost:54322/postgres

For detailed setup instructions, see [Development Setup](#development-setup) below.

---

## Table of Contents
1. [Quick Start](#quick-start)
2. [Project Overview](#project-overview)
3. [Architecture](#architecture)
4. [Backend Documentation](#backend-documentation)
5. [Frontend Documentation](#frontend-documentation)
6. [Database Schema](#database-schema)
7. [API Documentation](#api-documentation)
8. [Authentication](#authentication)
9. [Deployment](#deployment)
10. [Development Setup](#development-setup)

## Project Overview

PhysioApp is a comprehensive physiotherapy and fitness application that provides personalized exercise recommendations using AI. The application helps users with injury recovery, fitness goals, and exercise tracking through an intelligent chatbot and personalized workout plans.

### Key Features
- **AI-Powered Exercise Recommendations**: Personalized workout plans based on user preferences and injury history
- **Real-time Exercise Tracking**: Session management with set/rep counting and progress tracking
- **Progress Analytics**: Calendar view of completed workouts with detailed exercise logs
- **AI Chatbot**: Physiotherapy expert chatbot for exercise and injury advice
- **User Profile Management**: Comprehensive user profiles with injury history and fitness goals

### Technology Stack
- **Frontend**: React 18 with TypeScript, CSS Modules
- **Backend**: Spring Boot 3.5 with Java 21
- **Database**: PostgreSQL with Supabase
- **AI Integration**: DeepSeek API for exercise recommendations and chat
- **Authentication**: JWT-based authentication

## Architecture

The application follows a three-tier architecture:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React Frontend│    │  Spring Boot    │    │   PostgreSQL    │
│   (Port 3001)   │◄──►│   Backend       │◄──►│   Database      │
│                 │    │   (Port 8080)   │    │   (Port 54322)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Component Architecture
- **Frontend**: Component-based architecture with React hooks
- **Backend**: RESTful API with Spring Boot controllers
- **Database**: Relational database with proper foreign key relationships

## Backend Documentation

### Core Controllers

#### ExerciseRecommendationController
**Location**: `backend/src/main/java/com/appyo/physioapp/backend/ExerciseRecommendationController.java`

**Purpose**: Handles AI-powered exercise plan generation and recommendation functionality.

**Key Methods**:
- `generateExercisePlan()`: Creates personalized exercise plans using AI
- `getUserData()`: Retrieves user profile data for plan personalization
- `buildPrompt()`: Constructs AI prompts combining user data and preferences
- `parseExercisePlan()`: Parses AI responses into structured exercise objects

**API Endpoints**:
- `POST /api/recommendations/exercise`: Generate personalized exercise plan

#### ExerciseLogController
**Location**: `backend/src/main/java/com/appyo/physioapp/backend/ExerciseLogController.java`

**Purpose**: Manages exercise session logging and retrieval functionality.

**Key Methods**:
- `logExerciseSession()`: Stores completed exercise sessions
- `getUserRecentLogs()`: Retrieves exercise logs by user and date range
- `buildSessionSummary()`: Creates comprehensive session summaries
- `addTargetedAreas()`: Determines muscle groups targeted by exercises

**API Endpoints**:
- `POST /api/exercise-logs`: Log completed exercise session
- `GET /api/exercise-logs/user/{userId}/recent`: Get recent exercise logs

#### UserProfileController
**Location**: `backend/src/main/java/com/appyo/physioapp/backend/UserProfileController.java`

**Purpose**: Manages user profile operations including retrieval and updates.

**Key Methods**:
- `getUserProfile()`: Retrieves complete user profile information
- `updateUserProfile()`: Updates specific user profile fields
- `getUserProfileById()`: Retrieves user profile by ID (internal use)

**API Endpoints**:
- `GET /api/user/profile`: Get current user profile
- `PUT /api/user/profile`: Update user profile
- `GET /api/user/profile/{userId}`: Get user profile by ID

#### ChatController
**Location**: `backend/src/main/java/com/appyo/physioapp/backend/ChatController.java`

**Purpose**: Manages AI-powered chatbot functionality for physiotherapy consultation.

**Key Methods**:
- `chat()`: Processes chat messages and generates AI responses
- Rate limiting implementation for abuse prevention

**API Endpoints**:
- `POST /api/chat`: Process chat messages

### Authentication

#### JwtUtil
**Location**: `backend/src/main/java/com/appyo/physioapp/auth/JwtUtil.java`

**Purpose**: Handles JWT token generation, validation, and user extraction.

**Key Methods**:
- `generateToken()`: Creates JWT tokens for authenticated users
- `validateToken()`: Validates JWT token authenticity
- `extractUsername()`: Extracts username from JWT token

### Database Configuration

#### DatabaseConfig
**Location**: `backend/src/main/java/com/appyo/physioapp/backend/config/DatabaseConfig.java`

**Purpose**: Configures database connection and JdbcTemplate.

## Frontend Documentation

### Core Components

#### PlanPage
**Location**: `physioapp-frontend/src/components/PlanPage.tsx`

**Purpose**: Main exercise plan management interface.

**Key Features**:
- AI-powered exercise plan generation
- Interactive plan stepper integration
- Real-time plan display
- Session management and workout initiation

**Key Functions**:
- `handleGeneratePlan()`: Generates personalized exercise plans using AI
- `handleStartAllExercises()`: Initiates workout session with all exercises
- `handleSelectExercise()`: Initiates workout session with single exercise
- `handleLogout()`: Handles user logout and data cleanup

#### ExerciseSessionPage
**Location**: `physioapp-frontend/src/components/ExerciseSessionPage.tsx`

**Purpose**: Manages active workout sessions with real-time tracking.

**Key Features**:
- Real-time exercise session tracking with timers
- Set and rep counting for each exercise
- Exercise completion and skip functionality
- Session pause/resume capability
- Automatic session logging to database

**Key Functions**:
- `handleSessionComplete()`: Completes workout session and logs data
- `handleExerciseComplete()`: Marks exercise as completed
- `handleSkipExercise()`: Marks exercise as skipped
- `moveToNextExercise()`: Advances to next exercise
- `togglePause()`: Handles session pause/resume

#### SummaryPage
**Location**: `physioapp-frontend/src/components/SummaryPage.tsx`

**Purpose**: Displays exercise progress and calendar view.

**Key Features**:
- Weekly calendar view of exercise logs
- Exercise completion details by day
- Progress tracking and analytics
- Muscle group targeting visualization

**Key Functions**:
- `fetchExerciseLogs()`: Retrieves exercise logs from backend
- `getExercisesForDay()`: Filters exercises for specific days
- `parseExerciseNotes()`: Parses exercise completion data

#### ProfilePage
**Location**: `physioapp-frontend/src/components/ProfilePage.tsx`

**Purpose**: Manages user profile information and preferences.

**Key Features**:
- User profile display and editing
- Fitness goal and injury history management
- Equipment access preferences
- Profile data validation

### State Management

The application uses React hooks for state management:
- `useState`: Local component state
- `useEffect`: Side effects and data fetching
- `useHistory`: Navigation management
- `localStorage`: Persistent data storage

### Data Flow

1. **User Authentication**: JWT tokens stored in localStorage
2. **Profile Management**: User data fetched from backend API
3. **Plan Generation**: AI service generates personalized plans
4. **Session Tracking**: Exercise data logged to database
5. **Progress Display**: Historical data retrieved and displayed

## Database Schema

### Core Tables

#### user
Primary user profile information.
```sql
CREATE TABLE "user" (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100),
    email VARCHAR(100),
    password_hash VARCHAR(255) NOT NULL,
    age INTEGER,
    gender VARCHAR(20),
    weight DECIMAL(5,2),
    height DECIMAL(5,2),
    fitness_goal TEXT,
    injury_history TEXT,
    chronic_diseases TEXT,
    equipment_access TEXT,
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'MEMBER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);
```

#### workout_log
Exercise session logging and tracking.
```sql
CREATE TABLE workout_log (
    log_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES "user"(user_id),
    plan_id UUID REFERENCES workout_plan(plan_id),
    workout_date TIMESTAMP,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_minutes BIGINT,
    targeted_areas TEXT,
    notes TEXT
);
```

#### injury
User injury history and restrictions.
```sql
CREATE TABLE injury (
    injury_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES "user"(user_id),
    injury_type VARCHAR(100),
    injury_area VARCHAR(100),
    severity VARCHAR(50),
    recovery_status VARCHAR(50),
    muscle_groups_to_avoid TEXT,
    goals_for_injury TEXT,
    notes TEXT
);
```

## API Documentation

### Authentication Endpoints

#### POST /auth/signup
Register a new user account.
```json
{
  "username": "string",
  "password": "string",
  "email": "string (optional)"
}
```

#### POST /auth/login
Authenticate user and receive JWT token.
```json
{
  "username": "string",
  "password": "string"
}
```

### Exercise Recommendation Endpoints

#### POST /api/recommendations/exercise
Generate personalized exercise plan.
```json
{
  "userId": "uuid",
  "stepperData": {
    "fitnessGoal": "string",
    "equipmentAccess": "string",
    "injuryAreas": ["string"],
    "fitnessLevel": "string",
    "timeAvailable": "string",
    "preferredDays": ["string"]
  }
}
```

### Exercise Logging Endpoints

#### POST /api/exercise-logs
Log completed exercise session.
```json
{
  "userId": "uuid",
  "sessionStartTime": "ISO timestamp",
  "sessionEndTime": "ISO timestamp",
  "totalDuration": "number",
  "exercises": [
    {
      "exerciseName": "string",
      "setsCompleted": "number",
      "totalSets": "number",
      "reps": "string",
      "skipped": "boolean"
    }
  ]
}
```

#### GET /api/exercise-logs/user/{userId}/recent?days=7
Retrieve recent exercise logs.

### User Profile Endpoints

#### GET /api/user/profile
Get current user profile.

#### PUT /api/user/profile
Update user profile.
```json
{
  "name": "string",
  "age": "number",
  "fitnessGoal": "string",
  "equipmentAccess": "string"
}
```

### Chat Endpoints

#### POST /api/chat
Process chat message.
```json
{
  "message": "string"
}
```

## Authentication

### JWT Token Structure
- **Algorithm**: HS512
- **Expiration**: 1 hour
- **Claims**: username, issued at, expiration

### Token Usage
1. **Login**: User receives JWT token
2. **API Requests**: Token included in Authorization header
3. **Validation**: Backend validates token on each request
4. **Refresh**: User must re-login when token expires

### Security Features
- Password hashing with BCrypt
- JWT token validation
- Rate limiting on chat endpoints
- Input validation and sanitization

## Deployment

### Local Development
1. **Database**: Start Supabase local instance
2. **Backend**: Run Spring Boot application
3. **Frontend**: Start React development server

### Production Deployment
- **Database**: Supabase cloud hosting
- **Backend**: Spring Boot with Docker
- **Frontend**: React build with static hosting

### Environment Variables
```bash
# Database
DATABASE_URL=postgresql://postgres:postgres@localhost:54322/postgres

# JWT
JWT_SECRET=super-secret-jwt-token-with-at-least-32-characters-long

# AI Services
DEEPSEEK_API_KEY=your_deepseek_api_key
DEEPSEEK_API_URL=https://api.deepseek.com/v1
GEMINI_API_KEY=your_gemini_api_key
```

## Development Setup

### Prerequisites
- Java 21+ (OpenJDK recommended)
- Node.js 18+ with npm
- PostgreSQL 14+
- Docker (optional for Supabase local)
- Maven (included with project via mvnw wrapper)

### Installation Steps

#### Method 1: Using Supabase Local (Recommended)

1. **Clone Repository**
```bash
git clone <repository-url>
cd project-9-physioapp
```

2. **Install Supabase CLI**
```bash
# macOS
brew install supabase/tap/supabase

# Windows (with Chocolatey)
choco install supabase

# Linux
curl -fsSL https://get.supabase.com | sh
```

3. **Start Supabase Local Database**
```bash
cd supabase
supabase start
```

This will automatically:
- Start PostgreSQL on port 54322
- Apply all migrations from `supabase/migrations/`
- Run seed data if available
- Start Supabase Studio on http://localhost:54323

4. **Verify Database Setup**
```bash
# Check if database is running
supabase status

# Connect to database (optional)
psql postgresql://postgres:postgres@localhost:54322/postgres
```

#### Method 2: Manual PostgreSQL Setup

1. **Install PostgreSQL**
```bash
# macOS
brew install postgresql
brew services start postgresql

# Ubuntu/Debian
sudo apt update
sudo apt install postgresql postgresql-contrib

# Windows
# Download from https://www.postgresql.org/download/windows/
```

2. **Create Database and User**
```sql
-- Connect as postgres superuser
psql -U postgres

-- Create database
CREATE DATABASE physioapp_dev;

-- Create user (optional)
CREATE USER physioapp_user WITH PASSWORD 'physioapp_password';
GRANT ALL PRIVILEGES ON DATABASE physioapp_dev TO physioapp_user;

-- Exit psql
\q
```

3. **Run Database Migrations**
```bash
cd project-9-physioapp

# Apply migrations manually
psql -U postgres -d physioapp_dev -f supabase/migrations/20240101000000_initial_schema.sql
psql -U postgres -d physioapp_dev -f supabase/migrations/20240102000000_add_user_fields.sql
psql -U postgres -d physioapp_dev -f supabase/migrations/20240103000000_add_temp_data_tables.sql
psql -U postgres -d physioapp_dev -f supabase/migrations/20240104000000_add_exercise_sessions.sql
psql -U postgres -d physioapp_dev -f supabase/migrations/20240105000000_add_exercise_status_tracking.sql
psql -U postgres -d physioapp_dev -f supabase/migrations/20240106000000_fix_user_password_type.sql
psql -U postgres -d physioapp_dev -f supabase/migrations/20240107000000_use_lowercase_user_table.sql
```

4. **Configure Database Connection**
Create `backend/src/main/resources/application-dev.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/physioapp_dev
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Disable automatic schema creation
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```

### Environment Variables Setup

Create `.env` files in respective directories:

**Backend Environment** (`backend/.env`):
```bash
# Database
DATABASE_URL=postgresql://postgres:postgres@localhost:54322/postgres
DB_USER=postgres
DB_PASSWORD=postgres

# JWT Security
JWT_SECRET=super-secret-jwt-token-with-at-least-32-characters-long-for-security

# AI Services
DEEPSEEK_API_KEY=your_deepseek_api_key_here
DEEPSEEK_API_URL=https://api.deepseek.com/v1
GEMINI_API_KEY=your_gemini_api_key_here

# Server Configuration
SERVER_PORT=8080
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
```

**Frontend Environment** (`physioapp-frontend/.env`):
```bash
# API Configuration
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_API_TIMEOUT=10000

# Development
PORT=3000
GENERATE_SOURCEMAP=false
```

### Starting the Application

1. **Start Backend** (Terminal 1)
```bash
cd backend

# Make mvnw executable (Linux/macOS)
chmod +x mvnw

# Start Spring Boot application
./mvnw spring-boot:run

# Alternative: Use Maven directly
mvn spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

2. **Start Frontend** (Terminal 2)
```bash
cd physioapp-frontend

# Install dependencies
npm install

# Start React development server
npm start

# Or with custom port
PORT=3001 npm start
```

3. **Verify Setup**
```bash
# Check backend health
curl http://localhost:8080/api/health

# Check frontend
curl http://localhost:3000

# Test database connection
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"testpass123"}'
```

### Database Schema Overview

The application uses the following main tables:

#### Core Tables
- **`"User"`**: User profiles and authentication data
- **`exercise_sessions`**: Workout session logs
- **`exercise_logs`**: Individual exercise completion records
- **`user_preferences`**: User fitness preferences and goals
- **`generated_plans`**: AI-generated exercise plans

#### Migration Files (Applied in Order)
1. `20240101000000_initial_schema.sql` - Core tables and relationships
2. `20240102000000_add_user_fields.sql` - Additional user profile fields
3. `20240103000000_add_temp_data_tables.sql` - Temporary data structures
4. `20240104000000_add_exercise_sessions.sql` - Exercise tracking tables
5. `20240105000000_add_exercise_status_tracking.sql` - Session status tracking
6. `20240106000000_fix_user_password_type.sql` - Password field type fix
7. `20240107000000_use_lowercase_user_table.sql` - Table naming consistency

### Database Testing and Validation

**Quick Validation Commands:**
```bash
# Connect to database
psql postgresql://postgres:postgres@localhost:54322/postgres

# Verify tables exist
\dt

# Check specific table structure
\d "User"
\d exercise_sessions
\d exercise_logs

# Test sample queries
SELECT COUNT(*) FROM "User";
SELECT table_name FROM information_schema.tables WHERE table_schema='public';

# Exit psql
\q
```

**Test the Complete Authentication Flow:**
```bash
# 1. Test user registration
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"testpass123"}'

# Expected response: {"success":true,"message":"User created successfully","userId":"..."}

# 2. Test user login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass123"}'

# Expected response: {"success":true,"message":"Login successful","user":{...},"token":"..."}

# 3. Test exercise logging (replace TOKEN with actual JWT token from login)
curl -X POST http://localhost:8080/api/exercise-logs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "userId":"USER_ID",
    "sessionStartTime":"2025-06-22T13:00:00.000Z",
    "sessionEndTime":"2025-06-22T13:30:00.000Z",
    "totalDuration":1800,
    "status":"completed",
    "exercises":[{
      "exerciseName":"Push-ups",
      "setsCompleted":3,
      "totalSets":3,
      "reps":"15",
      "skipped":false,
      "completed":true
    }]
  }'

# Expected response: {"success":true,"sessionId":"...","exercisesCompleted":1,...}
```

**Verify Database Data:**
```sql
-- Connect to database and check data
psql postgresql://postgres:postgres@localhost:54322/postgres

-- Check created user
SELECT username, email, created_at FROM "User" WHERE username = 'testuser';

-- Check logged exercise sessions
SELECT session_date, exercise_name, sets, reps 
FROM exercise_logs 
WHERE user_id = (SELECT user_id FROM "User" WHERE username = 'testuser');

-- Exit
\q
```

### Common Issues and Troubleshooting

#### Port Conflicts
```bash
# Check what's using port 8080
lsof -i :8080

# Kill Spring Boot processes
pkill -f "spring-boot:run"

# Check port 54322 (PostgreSQL)
lsof -i :54322
```

#### Database Connection Issues
```bash
# Check Supabase status
supabase status

# Restart Supabase
supabase stop
supabase start

# Reset database (WARNING: Deletes all data)
supabase db reset
```

#### Maven/Java Issues
```bash
# Check Java version
java -version

# Check Maven version
./mvnw -version

# Clear Maven cache
./mvnw clean

# Force re-download dependencies
./mvnw clean install -U
```

### Development Workflow

1. **Feature Development**
   - Create feature branch from main
   - Set up local development environment
   - Make code changes
   - Test locally with both backend and frontend
   - Create pull request

2. **Database Changes**
   - Create new migration file in `supabase/migrations/`
   - Follow naming convention: `YYYYMMDDHHMMSS_description.sql`
   - Test migration locally
   - Include migration in pull request

3. **Testing**
   - Test authentication flow (signup/login)
   - Test exercise logging functionality
   - Test AI plan generation
   - Verify database persistence
   - Check frontend-backend integration
   - Unit tests for backend services
   - Integration tests for API endpoints
   - Manual testing of user flows

3. **Code Quality**
   - Prettier for code formatting
   - ESLint for JavaScript/TypeScript
   - Java code style guidelines

### Common Issues and Solutions

1. **Database Connection Issues**
   - Verify Supabase is running
   - Check database URL configuration
   - Ensure proper table schema

2. **JWT Token Issues**
   - Verify JWT secret configuration
   - Check token expiration
   - Validate token format

3. **AI Service Issues**
   - Verify API keys are configured
   - Check API rate limits
   - Validate request formats

## Contributing

### Code Standards
- Follow existing code style and patterns
- Add comprehensive documentation
- Include error handling
- Write meaningful commit messages

### Testing
- Test all new features thoroughly
- Verify API endpoints work correctly
- Test user flows end-to-end
- Check for edge cases and error conditions

---

**Last Updated**: January 2025
**Version**: 1.0
**Team**: PhysioApp Development Team 