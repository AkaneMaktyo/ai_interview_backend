package com.example.aiinterview.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from AI Interview Backend!";
    }

    @GetMapping("/status")
    public String status() {
        return "Backend is running successfully";
    }
}