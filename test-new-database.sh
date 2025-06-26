#!/bin/bash

# Test script for new database connection
echo "🚀 Testing new Supabase database connection..."

# Set environment variables for testing
export NEW_DB_PASSWORD="your-password-here"
export SPRING_PROFILES_ACTIVE="newdb"

echo "📋 Step 1: Set your database password"
echo "Please replace 'your-password-here' with your actual database password in the NEW_DB_PASSWORD environment variable"
echo ""

echo "📊 Step 2: Apply schema to your new Supabase database"
echo "Run this SQL in your Supabase SQL Editor:"
echo "File: supabase/new-database-schema.sql"
echo ""

echo "🧪 Step 3: Test locally with new database"
echo "Run: cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=newdb"
echo ""

echo "✅ Step 4: Test API endpoints"
echo "1. Test health: curl http://localhost:8080/actuator/health"
echo "2. Test registration: curl -X POST http://localhost:8080/api/auth/register -H 'Content-Type: application/json' -d '{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\"}'"
echo "3. Check logs for any database errors"
echo ""

echo "🚀 Step 5: Deploy to production"
echo "Update Render environment variables:"
echo "SPRING_DATASOURCE_URL=jdbc:postgresql://db.rjyvhwsjfakcttpdlwfy.supabase.co:5432/postgres?sslmode=require"
echo "SPRING_DATASOURCE_USERNAME=postgres"
echo "SPRING_DATASOURCE_PASSWORD=your-actual-password"
echo ""

echo "📝 Connection Options Available:"
echo ""
echo "🏆 RECOMMENDED: Direct Connection (for persistent Spring Boot apps)"
echo "   jdbc:postgresql://db.rjyvhwsjfakcttpdlwfy.supabase.co:5432/postgres?sslmode=require"
echo "   - Best for long-lived connections"
echo "   - Dedicated connection to Postgres"
echo "   - No IPv4 issues on most platforms"
echo ""
echo "🔄 ALTERNATIVE: Session Pooler (if IPv4 compatibility needed)"
echo "   jdbc:postgresql://aws-0-ca-central-1.pooler.supabase.com:5432/postgres?sslmode=require"
echo "   - Username: postgres.rjyvhwsjfakcttpdlwfy"
echo "   - IPv4 compatible"
echo "   - Connection limits may apply"
echo ""
echo "⚠️  NOT RECOMMENDED: Transaction Pooler"
echo "   jdbc:postgresql://aws-0-ca-central-1.pooler.supabase.com:6543/postgres?sslmode=require"
echo "   - Username: postgres.rjyvhwsjfakcttpdlwfy"
echo "   - Does not support prepared statements (Hibernate needs these)"
echo ""

echo "🔧 Troubleshooting:"
echo "- If connection fails, check your password"
echo "- If prepared statement errors, use Direct Connection or Session Pooler"
echo "- If connection limit errors, reduce hikari.maximum-pool-size"
echo "- Check Supabase dashboard for connection usage" 