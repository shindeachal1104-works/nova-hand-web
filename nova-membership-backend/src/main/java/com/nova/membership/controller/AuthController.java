package com.nova.membership.controller;

import com.nova.membership.dto.*;
import com.nova.membership.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final MemberService service;

    public AuthController(MemberService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ApiResponse login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Login successful.", service.login(request));
    }

    @GetMapping("/me")
    public ApiResponse me(Authentication authentication) {
        return ApiResponse.ok("Authenticated member.", service.getByEmail(authentication.getName()));
    }
}
