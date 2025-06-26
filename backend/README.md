# PhysioApp Backend

This is the backend service for the PhysioApp application, built with Spring Boot and PostgreSQL.

## Prerequisites

- Java 17 or higher
- Maven
- PostgreSQL database (hosted on Supabase)

## Environment Variables

Create a `.env` file in the root directory with the following variables:

```env
SUPABASE_DB_URL=jdbc:postgresql://your-supabase-host:5432/postgres
SUPABASE_DB_USER=your-supabase-user
SUPABASE_DB_PASSWORD=your-supabase-password
```

## Building and Running

1. Build the project:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

The application will start on port 8080.

## API Endpoints

### User Management

#### Register a new user
```
POST /api/users/register
Content-Type: application/json

{
    "username": "string",
    "email": "string",
    "password": "string",
    "name": "string"
}
```

#### Login
```
POST /api/users/login
Content-Type: application/json

{
    "usernameOrEmail": "string",
    "password": "string"
}
```

#### Get user profile
```
GET /api/users/{userId}
```

#### Update user profile
```
PUT /api/users/{userId}
Content-Type: application/json

{
    "name": "string",
    "gender": "string",
    "age": number,
    "phone": "string",
    "height": number,
    "weight": number,
    "chronicDiseases": "string",
    "injuryHistory": "string",
    "fitnessGoal": "string",
    "equipmentAccess": "string"
}
```

#### Change password
```
POST /api/users/{userId}/change-password
Content-Type: application/json

{
    "currentPassword": "string",
    "newPassword": "string"
}
```

#### Delete user
```
DELETE /api/users/{userId}
```

## Project Structure

```
src/main/java/com/example/physioapp/backend/
├── application/
│   └── service/
│       └── UserService.java
├── domain/
│   ├── entity/
│   │   └── User.java
│   └── repository/
│       └── UserRepository.java
├── infrastructure/
│   ├── config/
│   │   └── SecurityConfig.java
│   └── repository/
│       └── JdbcUserRepository.java
└── interfaces/
    ├── controller/
    │   └── UserController.java
    └── dto/
        └── UserDTOs.java
```

## Security

- Passwords are hashed using BCrypt
- CORS is enabled for development
- Input validation is performed on all endpoints

## Error Handling

The API returns appropriate HTTP status codes and error messages:

- 200: Success
- 400: Bad Request (invalid input)
- 401: Unauthorized
- 404: Not Found
- 500: Internal Server Error

Error responses include a message explaining the error:

```json
{
    "message": "Error message"
}
``` 