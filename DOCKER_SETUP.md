# Docker Setup for PhysioApp

## Overview
This document explains how to run PhysioApp using Docker containers. The setup includes the backend Spring Boot application, React frontend, and PostgreSQL database.

## Prerequisites
- Docker Desktop installed and running
- Docker Compose (usually included with Docker Desktop)
- At least 4GB of available RAM

## Quick Start

### 1. **Start All Services**
```bash
# Start the entire application stack
docker-compose up -d

# View logs
docker-compose logs -f
```

### 2. **Access the Application**
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Database**: localhost:54322
- **Health Check**: http://localhost:8080/api/health

### 3. **Stop All Services**
```bash
docker-compose down
```

## Service Architecture

### 🐳 **Container Services**

| Service | Port | Description |
|---------|------|-------------|
| **frontend** | 3000 | React application (Nginx) |
| **backend** | 8080 | Spring Boot API |
| **postgres** | 54322 | PostgreSQL database |
| **nginx** | 80/443 | Reverse proxy (production profile) |

### 🔧 **Service Dependencies**
```
frontend → backend → postgres
    ↓         ↓         ↓
  nginx → backend → postgres
```

## Detailed Usage

### **Development Mode**
```bash
# Start with development settings
SPRING_JPA_SHOW_SQL=true LOGGING_LEVEL=DEBUG docker-compose up -d

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

### **Production Mode**
```bash
# Start with production profile (includes nginx)
docker-compose --profile production up -d
```

### **Individual Services**
```bash
# Start only database
docker-compose up -d postgres

# Start only backend
docker-compose up -d backend

# Start only frontend
docker-compose up -d frontend
```

## Database Management

### **Database Initialization**
The database is automatically initialized with:
- Schema from `supabase/schema.sql`
- Seed data from `supabase/seed.sql`

### **Database Access**
```bash
# Connect to database container
docker-compose exec postgres psql -U postgres -d postgres

# View database logs
docker-compose logs postgres

# Reset database
docker-compose down -v
docker-compose up -d postgres
```

### **Database Backup**
```bash
# Create backup
docker-compose exec postgres pg_dump -U postgres postgres > backup.sql

# Restore backup
docker-compose exec -T postgres psql -U postgres postgres < backup.sql
```

## Environment Variables

### **Backend Environment Variables**
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/postgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# JWT
JWT_SECRET=super-secret-jwt-token-with-at-least-32-characters-long

# AI API
DEEPSEEK_API_KEY=your-api-key-here

# Logging
LOGGING_LEVEL=INFO
SPRING_JPA_SHOW_SQL=false
```

### **Frontend Environment Variables**
```bash
# API URL
REACT_APP_API_URL=http://localhost:8080

# Environment
NODE_ENV=production
```

## Docker Commands Reference

### **Build Commands**
```bash
# Build all services
docker-compose build

# Build specific service
docker-compose build backend
docker-compose build frontend

# Force rebuild (no cache)
docker-compose build --no-cache
```

### **Management Commands**
```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# Restart services
docker-compose restart

# View running containers
docker-compose ps

# View logs
docker-compose logs

# Execute commands in containers
docker-compose exec backend sh
docker-compose exec postgres psql -U postgres
```

### **Cleanup Commands**
```bash
# Remove containers and networks
docker-compose down

# Remove containers, networks, and volumes
docker-compose down -v

# Remove all images
docker-compose down --rmi all

# Clean up unused resources
docker system prune -a
```

## Troubleshooting

### **Common Issues**

#### 1. **Port Conflicts**
```bash
# Check what's using the ports
lsof -i :3000
lsof -i :8080
lsof -i :54322

# Kill processes if needed
kill -9 <PID>
```

#### 2. **Database Connection Issues**
```bash
# Check database health
docker-compose exec postgres pg_isready -U postgres

# Restart database
docker-compose restart postgres
```

#### 3. **Backend Startup Issues**
```bash
# Check backend logs
docker-compose logs backend

# Check if database is ready
docker-compose exec postgres pg_isready -U postgres
```

#### 4. **Frontend Build Issues**
```bash
# Rebuild frontend
docker-compose build --no-cache frontend

# Check frontend logs
docker-compose logs frontend
```

### **Performance Issues**
```bash
# Check resource usage
docker stats

# Increase memory for Docker Desktop
# (In Docker Desktop settings)
```

## Development Workflow

### **Making Changes**
1. **Code Changes**: Edit files in your IDE
2. **Rebuild**: `docker-compose build <service>`
3. **Restart**: `docker-compose restart <service>`
4. **Test**: Access the application

### **Database Changes**
1. **Update Schema**: Edit `supabase/schema.sql`
2. **Create Migration**: Add to `supabase/migrations/`
3. **Reset Database**: `docker-compose down -v && docker-compose up -d`

### **Adding Dependencies**
1. **Backend**: Update `pom.xml`, rebuild
2. **Frontend**: Update `package.json`, rebuild

## Production Deployment

### **Environment Setup**
```bash
# Create production environment file
cp .env.example .env.prod

# Edit production variables
nano .env.prod
```

### **Production Commands**
```bash
# Build for production
docker-compose -f docker-compose.yml -f docker-compose.prod.yml build

# Deploy with production profile
docker-compose -f docker-compose.yml -f docker-compose.prod.yml --profile production up -d
```

### **SSL/HTTPS Setup**
1. Add SSL certificates to `nginx/ssl/`
2. Update nginx configuration
3. Use production profile

## Monitoring and Logs

### **Log Management**
```bash
# View all logs
docker-compose logs

# View specific service logs
docker-compose logs backend
docker-compose logs frontend
docker-compose logs postgres

# Follow logs in real-time
docker-compose logs -f

# View logs with timestamps
docker-compose logs -t
```

### **Health Checks**
- **Backend**: http://localhost:8080/api/health
- **Frontend**: http://localhost:3000/health
- **Database**: `docker-compose exec postgres pg_isready -U postgres`

## Security Considerations

### **Production Security**
- Change default passwords
- Use environment variables for secrets
- Enable SSL/TLS
- Regular security updates
- Network isolation

### **Development Security**
- Don't commit secrets to git
- Use .env files for local development
- Regular dependency updates

---

**Last Updated**: January 2025  
**Docker Version**: 20.10+  
**Compose Version**: 2.0+ 