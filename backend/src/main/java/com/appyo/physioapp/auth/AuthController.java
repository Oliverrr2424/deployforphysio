package com.appyo.physioapp.auth;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.appyo.physioapp.user.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appyo.physioapp.auth.AuthRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository userRepo, JwtUtil jwtUtil, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestBody AuthRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (userRepo.findByUsername(request.getUsername()) != null) {
                response.put("success", false);
                response.put("message", "Username already exists");
                return response;
            }

            // Validate email is provided
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return response;
            }

            // Normalize email before checking for duplicates
            String normalizedEmail = request.getEmail().trim().toLowerCase();
            if (userRepo.findByEmail(normalizedEmail) != null) {
                response.put("success", false);
                response.put("message", "Email already in use");
                return response;
            }

            // Create user object with UUID
            User user = new User(request.getUsername(), request.getEmail(), encoder.encode(request.getPassword()));
            user.setRole("MEMBER");
            
            userRepo.save(user);
            
            response.put("success", true);
            response.put("message", "User created successfully");
            response.put("userId", user.getUserId().toString());
            return response;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Handles any constraint violations that slipped through our manual checks
            response.put("success", false);
            response.put("message", "User already exists (constraint violation)");
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Signup failed: " + e.getMessage());
            return response;
        }
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody AuthRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            User user = userRepo.findByUsername(request.getUsername());
            if (user == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            if (!encoder.matches(request.getPassword(), user.getPasswordHash())) {
                response.put("success", false);
                response.put("message", "Invalid password");
                return response;
            }

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepo.save(user);

            String token = jwtUtil.generateToken(user.getUsername());
            
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("user", Map.of(
                "userId", user.getUserId().toString(),
                "username", user.getUsername(),
                "email", user.getEmail() != null ? user.getEmail() : "",
                "name", user.getName() != null ? user.getName() : "",
                "role", user.getRole()
            ));
            
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login error: " + e.getMessage());
            return response;
        }
    }

    @PostMapping("/validate")
    public Map<String, Object> validateToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String token = authHeader.replace("Bearer ", "");
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                User user = userRepo.findByUsername(username);
                
                if (user != null) {
                    response.put("success", true);
                    response.put("user", Map.of(
                        "userId", user.getUserId().toString(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "name", user.getName(),
                        "role", user.getRole()
                    ));
                } else {
                    response.put("success", false);
                    response.put("message", "User not found");
                }
            } else {
                response.put("success", false);
                response.put("message", "Invalid token");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Token validation failed");
        }
        
        return response;
    }
    

}