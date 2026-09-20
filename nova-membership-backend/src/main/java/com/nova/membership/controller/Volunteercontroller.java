package com.nova.membership.controller;

import com.nova.membership.dto.ApiResponse;
import com.nova.membership.dto.VolunteerRequest;
import com.nova.membership.dto.VolunteerStatusRequest;
import com.nova.membership.service.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class VolunteerController {

    private final VolunteerService service;

    public VolunteerController(VolunteerService service) {
        this.service = service;
    }

    /** Public: anyone (member or not) can apply. */
    @PostMapping("/api/volunteers/apply")
    public ResponseEntity<ApiResponse> apply(@Valid @RequestBody VolunteerRequest request) {
        VolunteerService.ApplyResult result = service.apply(request);
        String message = result.alreadyApplied()
                ? "You have already applied. Thank you!"
                : "Volunteer application submitted.";
        HttpStatus status = result.alreadyApplied() ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(ApiResponse.ok(message, result));
    }

    /** Admin only: all applications (optionally ?status=PENDING). */
    @GetMapping("/api/admin/volunteers")
    public ApiResponse list(@RequestParam(required = false) String status) {
        return ApiResponse.ok("Volunteer applications.", service.list(status));
    }

    /** Admin only: approve / reject an application. */
    @PatchMapping("/api/admin/volunteers/{id}/status")
    public ApiResponse updateStatus(@PathVariable Long id, @Valid @RequestBody VolunteerStatusRequest request) {
        return ApiResponse.ok("Status updated.", service.updateStatus(id, request.status()));
    }
}