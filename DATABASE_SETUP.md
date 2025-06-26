# Database Setup Documentation

## Overview
PhysioApp uses **Supabase** as the primary database solution with **PostgreSQL** as the underlying database engine. We're running Supabase locally for development.

## Database Configuration

### 🗄️ **Primary Database: Supabase PostgreSQL**
- **Type**: PostgreSQL 15
- **Host**: Local Supabase instance
- **Port**: 54322 (Database), 54321 (API)
- **Database**: postgres
- **Username**: postgres
- **Password**: postgres

### 📁 **Database Files Location**
```
supabase/
├── config.toml          # Supabase configuration
├── schema.sql           # Main database schema (372 lines)
├── seed.sql            # Initial data seeding (59 lines)
└── migrations/         # Database migration files
    ├── 20240101000000_initial_schema.sql
    ├── 20240102000000_add_user_fields.sql
    ├── 20240103000000_add_temp_data_tables.sql
    ├── 20240104000000_add_exercise_sessions.sql
    ├── 20240105000000_add_exercise_status_tracking.sql
    ├── 20240106000000_fix_user_password_type.sql
    └── 20240107000000_use_lowercase_user_table.sql
```

## Key Database Files Explained

### 1. **`supabase/schema.sql`** (Main Schema File)
- **Purpose**: Complete database schema definition
- **Size**: 372 lines
- **Contains**: All table definitions, indexes, constraints
- **Usage**: Reference for current database structure

### 2. **`supabase/seed.sql`** (Initial Data)
- **Purpose**: Populates database with initial test data
- **Size**: 59 lines
- **Contains**: Sample users, exercises, and test data
- **Usage**: Automatically runs during database reset

### 3. **Migration Files** (Version Control)
- **Purpose**: Track database schema changes over time
- **Order**: Applied chronologically by timestamp
- **Latest**: `20240107000000_use_lowercase_user_table.sql`

## Database Connection Details

### Backend Configuration
```properties
# From application.properties
spring.datasource.url=jdbc:postgresql://localhost:54322/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Supabase Local Setup
- **API URL**: http://localhost:54321
- **Studio URL**: http://localhost:54323
- **Database URL**: postgresql://postgres:postgres@localhost:54322/postgres

## How to Start the Database

### 1. **Start Supabase Locally**
```bash
cd supabase
supabase start
```

### 2. **Reset Database (if needed)**
```bash
supabase db reset
```

### 3. **View Database in Studio**
- Open: http://localhost:54323
- Login with default credentials

## Database Schema Overview

### Main Tables
1. **users** - User accounts and profiles
2. **exercise_logs** - Exercise session tracking
3. **exercise_sessions** - Detailed exercise data
4. **user_preferences** - User settings and preferences
5. **generated_plans** - AI-generated exercise plans

### Key Features
- **JPA/Hibernate**: ORM for Java backend
- **Migration System**: Version-controlled schema changes
- **Seeding**: Automatic test data population
- **Studio Interface**: Web-based database management

## Development Workflow

### Making Schema Changes
1. **Create Migration**: `supabase migration new <description>`
2. **Edit Migration**: Add SQL to the new migration file
3. **Apply Migration**: `supabase db reset` (applies all migrations)
4. **Update Schema**: `supabase db dump --schema public > schema.sql`

### Adding Test Data
1. **Edit**: `supabase/seed.sql`
2. **Reset**: `supabase db reset` (reapplies seed data)

## Important Notes

### ⚠️ **Current Configuration**
- **Local Development**: Supabase running on localhost
- **Production Ready**: Can be deployed to Supabase Cloud
- **Backup**: Schema and migrations are version controlled

### 🔧 **Backend Integration**
- **JPA/Hibernate**: Handles database operations
- **DDL Auto**: Set to `none` (migrations handle schema)
- **SQL Logging**: Enabled for debugging

### 📊 **Monitoring**
- **H2 Console**: Available at http://localhost:8080/h2-console (fallback)
- **Supabase Studio**: Primary database interface
- **Logs**: SQL queries logged in backend console

## Troubleshooting

### Common Issues
1. **Port Conflicts**: Ensure ports 54321-54324 are available
2. **Migration Errors**: Run `supabase db reset` to start fresh
3. **Connection Issues**: Check if Supabase is running with `supabase status`

### Reset Everything
```bash
supabase stop
supabase start
supabase db reset
```

## Team Communication

### For New Team Members
1. **Install Supabase CLI**: `npm install -g supabase`
2. **Start Database**: `cd supabase && supabase start`
3. **Check Status**: `supabase status`
4. **View Data**: Open http://localhost:54323

### For Database Changes
1. **Discuss**: Team review of schema changes
2. **Create Migration**: Document the change
3. **Test**: Verify in local environment
4. **Commit**: Include migration files in git

---

**Last Updated**: January 2025  
**Maintained By**: PhysioApp Development Team  
**Database Version**: PostgreSQL 15 with Supabase 