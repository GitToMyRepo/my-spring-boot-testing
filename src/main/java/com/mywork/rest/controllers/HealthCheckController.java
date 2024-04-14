package com.mywork.rest.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@Slf4j
public class HealthCheckController {
    @GetMapping("/api/health")
    public String healthCheck() {
        log.info("Returning OK");
        return "OK";
    }
}
