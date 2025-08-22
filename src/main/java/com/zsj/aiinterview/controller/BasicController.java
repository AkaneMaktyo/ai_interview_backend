package com.zsj.aiinterview.controller;

import org.springframework.web.bind.annotation.*;

/**
 * 基础控制器 - 最小可行版本
 */
@RestController
@RequestMapping("/api")

public class BasicController {

    @GetMapping("/hello")
    public String hello() {
        return "AI Interview Backend is running! 🚀";
    }

    @GetMapping("/status")
    public String status() {
        return "Backend Status: ✅ Running on port 8080";
    }
    
    @PostMapping("/test")
    public String test(@RequestParam(required = false) String message) {
        return "Test successful! Message: " + (message != null ? message : "No message provided");
    }
}