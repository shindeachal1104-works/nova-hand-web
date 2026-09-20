package com.nova.membership.service;

import com.nova.membership.dto.VolunteerRequest;
import com.nova.membership.dto.VolunteerResponse;
import com.nova.membership.entity.VolunteerApplication;
import com.nova.membership.repository.VolunteerApplicationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class VolunteerService {

    private static final Set<String> STATUSES = Set.of("PENDING", "APPROVED", "REJECTED");

    private final VolunteerApplicationRepository repository;

    public VolunteerService(VolunteerApplicationRepository repository) {
        this.repository = repository;
    }

    public record ApplyResult(Long id, String status, boolean alreadyApplied) {}

    /**
     * Saves a volunteer application. Applying twice with the same mobile number does NOT create a
     * duplicate - the earlier application is returned (this also makes the "share with 5 friends"
     * auto-registration safe to retry).
     */
    @Transactional
    public ApplyResult apply(VolunteerRequest r) {
        String mobile = normalizeMobile(r.mobile());
        if (!mobile.matches("^\\+?[0-9]{10,15}$")) {
            throw new IllegalArgumentException("Enter a valid mobile number.");
        }

        var existing = repository.findByMobile(mobile);
        if (existing.isPresent()) {
            VolunteerApplication e = existing.get();
            return new ApplyResult(e.getId(), e.getStatus(), true);
        }

        VolunteerApplication v = new VolunteerApplication();
        v.setName(r.name().trim());
        v.setMobile(mobile);
        v.setAge(r.age());
        v.setCity(blankToNull(r.city()));
        v.setOccupation(blankToNull(r.occupation()));
        v.setInterests(blankToNull(r.interests()));
        v.setReason(blankToNull(r.reason()));
        v.setAvailability(blankToNull(r.availability()));
        v.setVolunteerType(blankToNull(r.volunteerType()));
        v.setSource(blankToNull(r.source()) == null ? "form" : r.source().trim());
        v.setReferredBy(blankToNull(r.referredBy()));

        VolunteerApplication saved = repository.save(v);
        return new ApplyResult(saved.getId(), saved.getStatus(), false);
    }

    public List<VolunteerResponse> list(String status) {
        List<VolunteerApplication> found;
        if (status == null || status.isBlank()) {
            found = repository.findAllByOrderByCreatedAtDesc();
        } else {
            found = repository.findByStatusOrderByCreatedAtDesc(status.trim().toUpperCase(Locale.ROOT));
        }
        return found.stream().map(VolunteerResponse::from).toList();
    }

    @Transactional
    public VolunteerResponse updateStatus(Long id, String status) {
        String s = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        if (!STATUSES.contains(s)) {
            throw new IllegalArgumentException("Status must be PENDING, APPROVED or REJECTED.");
        }
        VolunteerApplication v = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found."));
        v.setStatus(s);
        return VolunteerResponse.from(repository.save(v));
    }

    private static String normalizeMobile(String raw) {
        return raw == null ? "" : raw.trim().replaceAll("[\\s()\\-]", "");
    }

    private static String blankToNull(String v) {
        return v == null || v.isBlank() ? null : v.trim();
    }
}