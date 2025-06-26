package com.appyo.physioapp.backend.presentation.controller;

import com.appyo.physioapp.auth.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public Map<String, Object> testRegister(@RequestBody Map<String, Object> request) {
        return Map.of(
            "success", true,
            "message", "Test registration successful",
            "received", request
        );
    }
    
    @PostMapping("/login")
    public Map<String, Object> testLogin(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String token = jwtUtil.generateToken(username);
        
        return Map.of(
            "success", true,
            "message", "Test login successful", 
            "token", token,
            "user", Map.of(
                "userId", "test-uuid",
                "username", username,
                "role", "MEMBER"
            )
        );
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "message", "Backend is running");
    }
} 