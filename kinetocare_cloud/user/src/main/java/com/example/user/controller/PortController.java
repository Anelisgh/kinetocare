package com.example.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Controller pentru a demonstra scalabilitatea
@RestController
public class PortController {
    @Value("${server.port}")
    private String port;

    @GetMapping("/port")
    public ResponseEntity<String> getPort() {
        return ResponseEntity.ok(port);
    }
}
