package com.nova.membership.controller;

import com.nova.membership.dto.*;
import com.nova.membership.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> register(@Valid @ModelAttribute RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("NOVA membership created successfully.", service.register(request)));
    }

    @GetMapping("/me")
    public ApiResponse me(Authentication authentication) {
        return ApiResponse.ok("Authenticated member.", service.getByEmail(authentication.getName()));
    }
}
