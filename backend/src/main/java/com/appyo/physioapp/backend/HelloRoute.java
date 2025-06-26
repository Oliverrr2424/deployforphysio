package com.appyo.physioapp.backend; // adjust to match your folder structure

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloRoute {

    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring Boot!";
    }
}