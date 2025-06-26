[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/yHhFu887)

# PhysioApp

Partner Email: Hello@appyyo.com
We are specifically working with Nadia who has a background in IT Project Management

## Description of Project:

Physioapp is a rehabilitation-focused workout generator that delivers to users evidence-based gym routines in seconds, from the comfort of their own home. Simply choose 
the muscles you want to either strengthen, stretch or avoid and list the equipment you have accessible to you. Physioapp will then deliver a physiotherapist-approved plan complete with 
animated demos, a progress-logging system, and an AI coach always ready on stand-by. In short, less clinic time, much faster recovery and workouts you can trust, offered by professionals

## Key Proposed Features:
- A log of exercises that the individual has performed in the past
- The ability to specify which muscle groups to strengthen, stretch, or exclude in an exercise routine and get exercises recommended accordingly
- Exercise recommendations for the duration of 10, 20, or 30 minutes
- An AI chatbot willing to answer questions you have
- An animated model depicting how to perform exercises
- Link to Figma mockup: https://www.figma.com/design/1zJLOtmis22ncxjXJYR8Pc/D1-prototype?node-id=0-1&p=f&t=39F4INJo8jsx1qt8-0
  

## Screenshots:
<div style="display: flex; gap: 10px; flex-wrap: wrap;">
  <img width="45%" alt="login" src="https://github.com/user-attachments/assets/f18c52db-b565-4ed0-b3ab-c49c21bc0a6d" />
  <img width="45%" alt="homepage" src="https://github.com/user-attachments/assets/7212d956-4f71-4431-89e8-d4f924dab43a"/>
  <img width="45%" alt="Exercise_log" src="https://github.com/user-attachments/assets/5fe17e4c-0be5-4b23-a173-87c0e39b5c27" />
  <img width="45%" alt="profile_page" src="https://github.com/user-attachments/assets/597a192c-8228-4589-839b-e2a3d2a15284" />
  <img width="45%" alt="generated_plan" src="https://github.com/user-attachments/assets/2e3178e3-321b-4524-8716-88fda02b46c4" />
  <img width="45%" alt="AI_assistant" src="https://github.com/user-attachments/assets/757a6ab5-d66a-4efd-8e3e-a355c078e5fc" />
</div>

## Video Demo


https://github.com/user-attachments/assets/430314a3-9266-4142-9583-4f945e13cae9



## Instructions:

1. The user must first make an account
2. They will then select which muscle group to include or exclude
3. The app will generate recommendations off of this input
4. It will record the exercises the user chooses, which are to be kept in its log
5. If the user has any questions, they can ask the chatbot or follow along with the animated model
Throughout this process, users can communicate with the chatbot, an AI physiotherapist expert on anything they desire, ranging from how to perform certain exercises to asking how to alleviate pain in certain areas. The chatbot is designed to be able to help it;s patients. 

## How will tasks be managed?

The use of Jira to handle stories and tasks. They will also be brought up in weekly scrums for discussion. 

## Deployment and Github workflow 

We will be using either Heroku or Docker for deployment. When working on new features, teammates will make branches and write code there. Once they are done, they will submit a PR which will be reviewed by the team during the weekly scrums before it is merged into the main codebase. This ensures all members are aware and onboard with changes to the app. 

Current Branches:
- Main Branch represents the current, most updated version of our app. No testing or developing is done on the main branch, it only holds the version of the app that is ready to be deployed at any moment if there is no other dev changes to be added
- Dev branch is where new features are developed. Developers write code on their own branches, submit PR, where other developers will review their code and if features are implemented nicely, are then merged with the dev branch which will later be added to main to build on the app.
- Each developer maintains their own individual branch where they implement their required features, based on tasks assigned to them on JIRA.

## Docker Setup

PhysioApp can be run entirely using Docker containers. For detailed Docker setup instructions, see [DOCKER_SETUP.md](./DOCKER_SETUP.md).

### Quick Start with Docker
```bash
# Start the database first
npm install -g supabase
supabase start

# Start all services (backend, frontend)
docker compose -f docker-compose.simple.yml up --build -d

# Access the application
# Frontend: http://localhost:3000
# Backend: http://localhost:8080
# Database: http://localhost:54323 (Supabase Studio)

# Stop all services
docker compose -f docker-compose.simple.yml down
```

### Docker Services
- **Frontend**: React app served by Nginx (port 3000)
- **Backend**: Spring Boot API (port 8080)
- **Database**: PostgreSQL (port 54322)
- **Nginx**: Reverse proxy (production profile)

## Database Setup

PhysioApp uses **Supabase PostgreSQL** for data storage. For detailed database setup instructions, see [DATABASE_SETUP.md](./DATABASE_SETUP.md).

### Quick Start
```bash
# Install Supabase CLI
npm install -g supabase

# Start the database
cd supabase
supabase start

# View database in browser
open http://localhost:54323
```

### Database Files
- **Main Schema**: `supabase/schema.sql` (372 lines)
- **Initial Data**: `supabase/seed.sql` (59 lines)
- **Migrations**: `supabase/migrations/` (7 migration files)
- **Configuration**: `supabase/config.toml`

## Coding Standards

We plan to use Prettier to enforce consistent coding standards. This includes
- descriptive, short variable names
- consistently leaving comments explaining what blocks of code do
- leaving indents wherever possible  

## Docker Usage for Backend

You can run the backend Spring Boot application in a Docker container using Java 21, Spring Boot 3.5, and Maven Wrapper 3.3.2. This is useful for local development and for team members who do not want to install Java or Maven locally.

### Build and Run the Backend with Docker

1. Open a terminal and navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. Build the Docker image:
   ```bash
   docker build -t physioapp-backend .
   ```
3. Run the backend container:
   ```bash
   docker run -p 8080:8080 physioapp-backend
   ```
   The backend will be available at [http://localhost:8080](http://localhost:8080).

**Note:**
- Docker must be installed on your machine.
- The Dockerfile uses multi-stage builds for efficiency and uses your Maven Wrapper for reproducible builds.
- No need to install Java or Maven locally; Docker handles it all.

## Local Testing Guide

### Prerequisites
Before testing locally, ensure you have the following installed:
- **Docker** and **Docker Compose** (for containerized testing)
- **Node.js 18+** and **npm** (for frontend development)
- **Java 21** and **Maven** (for backend development)
- **Supabase CLI** (for database management)

### Option 1: Full Docker Testing (Recommended)

This is the easiest way to test the complete application:

```bash
# 1. Clone the repository
git clone <repository-url>
cd project-9-physioapp

# 2. Start the database
npm install -g supabase
supabase start

# 3. Build and start all services
docker compose -f docker-compose.simple.yml up --build

# 4. Test the application
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
# Database: http://localhost:54323 (Supabase Studio)

# 5. Stop all services
docker compose -f docker-compose.simple.yml down
```

### Option 2: Individual Component Testing

#### Database Setup
```bash
# Install and start Supabase
npm install -g supabase
cd supabase
supabase start

# View database in browser at http://localhost:54323
# Database URL: postgresql://postgres:postgres@localhost:54322/postgres
```

#### Backend Testing (Spring Boot)
```bash
# Option A: Using Docker
cd backend
docker build -t physioapp-backend .
docker run -p 8080:8080 physioapp-backend

# Option B: Using Maven (requires Java 21)
cd backend
./mvnw spring-boot:run

# Test backend endpoints
curl http://localhost:8080/api/temp-data
curl http://localhost:8080/api/auth/health
```

#### Frontend Testing (React)
```bash
# Install dependencies and start development server
cd physioapp-frontend
npm install
npm start

# Frontend will be available at http://localhost:3000
# Test frontend features:
# - User registration and login
# - Exercise recommendations
# - Chatbot functionality
```

### Testing Endpoints

#### Backend API Endpoints
```bash
# Health check
curl -X GET http://localhost:8080/actuator/health

# User registration
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'

# User login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# Get exercise recommendations (requires authentication)
curl -X POST http://localhost:8080/api/exercise-recommendations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{"duration":20,"strengthenMuscles":["legs"],"excludeMuscles":[]}'

# Chatbot interaction
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{"message":"What exercises can I do for my back?"}'
```

#### Frontend Testing Checklist
- [ ] **Registration**: Create new user account
- [ ] **Login**: Sign in with valid credentials
- [ ] **Exercise Selection**: Choose muscle groups to strengthen/exclude
- [ ] **Plan Generation**: Generate 10/20/30 minute workout plans
- [ ] **Exercise Logging**: Mark exercises as completed
- [ ] **Chatbot**: Ask questions about exercises
- [ ] **Profile Management**: Update user preferences
- [ ] **Navigation**: Test all page transitions

### Database Testing
```bash
# Connect to database directly
psql postgresql://postgres:postgres@localhost:54322/postgres

# View tables
\dt

# Check user data
SELECT * FROM users LIMIT 5;

# Check exercise data
SELECT * FROM generated_plans LIMIT 5;

# Exit psql
\q
```

### Troubleshooting Common Issues

#### Port Conflicts
```bash
# Check what's running on required ports
lsof -i :3000  # Frontend
lsof -i :8080  # Backend
lsof -i :54322 # Database

# Kill processes if needed
kill -9 <PID>
```

#### Docker Issues
```bash
# Clean up Docker resources
docker compose -f docker-compose.simple.yml down --volumes
docker system prune -f

# Rebuild containers
docker compose -f docker-compose.simple.yml up --build --force-recreate
```

#### Database Connection Issues
```bash
# Reset Supabase
supabase stop
supabase start

# Check database status
supabase status
```

### Environment Variables for Testing

Create a `.env` file in the backend directory for local testing:
```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:54322/postgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# JWT
JWT_SECRET=super-secret-jwt-token-for-testing-only

# AI API (optional for basic testing)
DEEPSEEK_API_KEY=your-api-key-here
```

### Performance Testing
```bash
# Load test the backend (requires Apache Bench)
ab -n 100 -c 10 http://localhost:8080/api/temp-data

# Monitor Docker resource usage
docker stats
```

### Testing with Sample Data

The application includes sample data in `supabase/seed.sql`. After starting the database, you can:

1. **Login with test user**: `testuser` / `password123`
2. **Try exercise generation**: Select "legs" to strengthen, "back" to exclude
3. **Test chatbot**: Ask "What exercises help with knee pain?"
4. **View exercise history**: Check the logging functionality

For any testing issues, check the logs:
```bash
# Docker logs
docker compose -f docker-compose.simple.yml logs backend
docker compose -f docker-compose.simple.yml logs frontend

# Supabase logs
supabase logs
```

## License:

I agree not to share or distribute the project idea, code, or related materials outside the scope of this course, except for the purposes of academic or professional interview.


