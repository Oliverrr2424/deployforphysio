package com.appyo.physioapp.auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.appyo.physioapp.user.User;
import java.util.UUID;

/**
 * JpaRepository is a predefined interface with methods to interact with the database.
 * The findByUsername method is a custom method that will be used to find a user by their username.
 * It is equivalent to writing a SQL query like "SELECT * FROM users WHERE username = ?"
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // finds the user by their username
    User findByUsername(String username);

    // finds the user by their email
    User findByEmail(String email);

    // finds the user by their user ID
    User findByUserId(UUID userId);

    // saves the user to the database, usually used after creating a new user object from the AuthController during signup
    User save(User user); 
}