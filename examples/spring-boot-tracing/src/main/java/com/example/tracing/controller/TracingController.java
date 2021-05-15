package com.example.tracing.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TracingController {

    @GetMapping("/trace")
    public ResponseEntity<String> getTrace(){
        log.info("GET /trace");
        return ResponseEntity.ok("This is a trace");
    }

}
