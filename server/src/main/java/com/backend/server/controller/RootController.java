package com.backend.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Spring Boot API is running");
        response.put("status", "OK");
        response.put("endpoints", Map.of(
            "auth", "/api/auth",
            "admin", "/admin"
        ));
        return ResponseEntity.ok(response);
    }
}
