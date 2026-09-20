package com.nova.membership.controller;

import com.nova.membership.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HealthController {
    @GetMapping("/health")
    public ApiResponse health() {
        return ApiResponse.ok("NOVA backend is running.", null);
    }
}
