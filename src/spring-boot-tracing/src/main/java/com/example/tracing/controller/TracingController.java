package com.example.tracing.controller;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TracingController {

    private final RestTemplate restTemplate;

    @GetMapping("/trace")
    @Timed("com.example.tracing.controller")
    public ResponseEntity<String> getTrace(){
        log.info("GET /trace");
        return ResponseEntity.ok("This is a trace");
    }

    @GetMapping("/tracee")
    @Timed("com.example.tracing.controller")
    public ResponseEntity<String> getTracee(){
        log.info("GET /tracee");
        return ResponseEntity.ok(restTemplate.getForObject("http://localhost:8080/trace", String.class));
    }

}
