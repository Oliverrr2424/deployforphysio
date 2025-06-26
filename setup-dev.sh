#!/bin/bash

# PhysioApp Development Environment Setup Script
# This script sets up the complete development environment for PhysioApp

set -e

echo "🏥 PhysioApp Development Environment Setup"
echo "==========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
print_status "Checking prerequisites..."

# Check Java
if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 21 ]; then
        print_success "Java $JAVA_VERSION detected"
    else
        print_error "Java 21+ required. Current version: $JAVA_VERSION"
        exit 1
    fi
else
    print_error "Java not found. Please install Java 21+"
    exit 1
fi

# Check Node.js
if command_exists node; then
    NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
    if [ "$NODE_VERSION" -ge 18 ]; then
        print_success "Node.js v$NODE_VERSION detected"
    else
        print_error "Node.js 18+ required. Current version: v$NODE_VERSION"
        exit 1
    fi
else
    print_error "Node.js not found. Please install Node.js 18+"
    exit 1
fi

# Check npm
if command_exists npm; then
    print_success "npm detected"
else
    print_error "npm not found. Please install npm"
    exit 1
fi

# Check if Supabase CLI is installed
if command_exists supabase; then
    print_success "Supabase CLI detected"
    USE_SUPABASE=true
else
    print_warning "Supabase CLI not found. Will use manual PostgreSQL setup"
    USE_SUPABASE=false
    
    # Check PostgreSQL
    if command_exists psql; then
        print_success "PostgreSQL detected"
    else
        print_error "PostgreSQL not found. Please install PostgreSQL or Supabase CLI"
        exit 1
    fi
fi

echo ""
print_status "Setting up database..."

if [ "$USE_SUPABASE" = true ]; then
    # Supabase setup
    print_status "Starting Supabase local development environment..."
    cd supabase
    supabase start
    print_success "Supabase started successfully"
    print_status "Database available at: postgresql://postgres:postgres@localhost:54322/postgres"
    print_status "Supabase Studio available at: http://localhost:54323"
    cd ..
else
    # Manual PostgreSQL setup
    print_status "Setting up PostgreSQL database manually..."
    
    # Create database
    print_status "Creating physioapp_dev database..."
    createdb physioapp_dev 2>/dev/null || print_warning "Database physioapp_dev may already exist"
    
    # Run migrations
    print_status "Running database migrations..."
    for migration in supabase/migrations/*.sql; do
        if [ -f "$migration" ]; then
            print_status "Applying $(basename "$migration")..."
            psql -d physioapp_dev -f "$migration" -q
        fi
    done
    print_success "Database migrations completed"
fi

echo ""
print_status "Setting up backend..."

# Backend setup
cd backend

# Make mvnw executable
chmod +x mvnw 2>/dev/null || true

# Create environment file
if [ ! -f .env ]; then
    print_status "Creating backend .env file..."
    cat > .env << EOF
# Database Configuration
DATABASE_URL=postgresql://postgres:postgres@localhost:54322/postgres
DB_USER=postgres
DB_PASSWORD=postgres

# JWT Security
JWT_SECRET=super-secret-jwt-token-with-at-least-32-characters-long-for-security

# AI Services (Add your API keys here)
DEEPSEEK_API_KEY=your_deepseek_api_key_here
DEEPSEEK_API_URL=https://api.deepseek.com/v1
GEMINI_API_KEY=your_gemini_api_key_here

# Server Configuration
SERVER_PORT=8080
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
EOF
    print_success "Backend .env file created"
else
    print_warning "Backend .env file already exists"
fi

cd ..

echo ""
print_status "Setting up frontend..."

# Frontend setup
cd physioapp-frontend

# Install dependencies
print_status "Installing frontend dependencies..."
npm install

# Create environment file
if [ ! -f .env ]; then
    print_status "Creating frontend .env file..."
    cat > .env << EOF
# API Configuration
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_API_TIMEOUT=10000

# Development
PORT=3000
GENERATE_SOURCEMAP=false
EOF
    print_success "Frontend .env file created"
else
    print_warning "Frontend .env file already exists"
fi

cd ..

echo ""
print_success "🎉 Setup completed successfully!"
echo ""
print_status "Next steps:"
echo "1. Update API keys in backend/.env file"
echo "2. Start the backend: cd backend && ./mvnw spring-boot:run"
echo "3. Start the frontend: cd physioapp-frontend && npm start"
echo ""
print_status "Application URLs:"
echo "- Frontend: http://localhost:3000"
echo "- Backend API: http://localhost:8080"
if [ "$USE_SUPABASE" = true ]; then
    echo "- Database: postgresql://postgres:postgres@localhost:54322/postgres"
    echo "- Supabase Studio: http://localhost:54323"
else
    echo "- Database: postgresql://localhost:5432/physioapp_dev"
fi
echo ""
print_status "For troubleshooting, refer to DEVELOPER_DOCUMENTATION.md" 