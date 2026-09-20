package com.nova.membership.service;


import com.nova.membership.dto.LoginRequest;
import com.nova.membership.dto.MemberResponse;
import com.nova.membership.dto.RegisterRequest;
import com.nova.membership.entity.Member;
import com.nova.membership.repository.MemberRepository;
import com.nova.membership.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository repository;
    private final FileStorageService fileStorageService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /** E-mails (lower-case) that are promoted to the ADMIN role - set ADMIN_EMAILS on the server. */
    private final Set<String> adminEmails;

    public MemberService(
            MemberRepository repository,
            FileStorageService fileStorageService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.emails:}") String adminEmails
    ) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.adminEmails = Arrays.stream(adminEmails.split(","))
                .map(e -> e.trim().toLowerCase(Locale.ROOT))
                .filter(e -> !e.isEmpty())
                .collect(Collectors.toSet());
    }

    /**
     * Registers the member and returns a JWT.
     * The password is stored as a BCrypt hash.
     */
    @Transactional
    public AuthResult register(RegisterRequest r) {

        String email = r.getEmail().trim().toLowerCase();
        String mobile = normalizeMobile(r.getMobileNumber());

        // Validate mobile number
        if (!mobile.matches("^\\+?[0-9]{10,15}$")) {
            throw new IllegalArgumentException(
                    "Enter a valid mobile number."
            );
        }

        // Check duplicate email
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "This email is already registered. Please log in."
            );
        }

        // Check duplicate mobile
        if (repository.existsByMobileNumber(mobile)) {
            throw new IllegalArgumentException(
                    "This mobile number is already registered. Please log in."
            );
        }

        Member m = new Member();

        // Required / basic information
        m.setFullName(
                r.getFullName() != null
                        ? r.getFullName().trim()
                        : null
        );

        m.setDateOfBirth(r.getDateOfBirth());

        m.setMobileNumber(mobile);

        m.setEmail(email);

        // Optional information
        m.setCity(
                r.getCity() != null
                        ? r.getCity().trim()
                        : null
        );

        m.setSchoolCollege(
                r.getSchoolCollege() != null
                        ? r.getSchoolCollege().trim()
                        : null
        );

        m.setClassName(
                r.getClassName() != null
                        ? r.getClassName().trim()
                        : null
        );

        m.setStream(
                r.getStream() != null
                        ? r.getStream().trim()
                        : null
        );

        m.setBoard(
                r.getBoard() != null
                        ? r.getBoard().trim()
                        : null
        );

        m.setAreasOfInterest(
                r.getAreasOfInterest() != null
                        ? r.getAreasOfInterest().trim()
                        : null
        );

        m.setGender(
                r.getGender() != null
                        ? r.getGender().trim()
                        : null
        );

        m.setDistrict(
                r.getDistrict() != null
                        ? r.getDistrict().trim()
                        : null
        );

        m.setPasswordHash(passwordEncoder.encode(r.getPassword()));

        // Members whose e-mail is in ADMIN_EMAILS become admins (can upload songs, review volunteers)
        if (adminEmails.contains(email)) {
            m.setRole("ADMIN");
        }

        // Terms
        m.setTermsAccepted(r.isTermsAccepted());

        // Profile photo
        m.setProfilePhotoUrl(
                fileStorageService.storeProfilePhoto(
                        r.getProfilePhoto()
                )
        );

        // Save member
        Member saved = repository.save(m);

        // Generate JWT
        String token = jwtService.generateToken(
                saved.getEmail()
        );

        return new AuthResult(
                token,
                MemberResponse.from(saved)
        );
    }

    /**
     * Login using email OR mobile number.
     */
    @Transactional
    public AuthResult login(LoginRequest r) {

        String id = r.identifier().trim();

        Optional<Member> found;

        if (id.contains("@")) {

            found = repository.findByEmailIgnoreCase(id);

        } else {

            found = repository.findByMobileNumber(
                    normalizeMobile(id)
            );
        }

        Member member = found
                .filter(m -> passwordMatches(r.password(), m.getPasswordHash()))
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email/mobile or password."
                        )
                );

        // Account status check
        if (!"ACTIVE".equals(member.getStatus())) {

            throw new IllegalStateException(
                    "Your account is not active. Please contact the NOVA team."
            );
        }

        // Old accounts still have a plain-text password: convert it to BCrypt now that we know it.
        if (!isBcrypt(member.getPasswordHash())) {
            member.setPasswordHash(passwordEncoder.encode(r.password()));
        }

        // Promote to ADMIN if this e-mail is listed in ADMIN_EMAILS
        if (adminEmails.contains(member.getEmail().toLowerCase(Locale.ROOT)) && !"ADMIN".equals(member.getRole())) {
            member.setRole("ADMIN");
        }
        repository.save(member);

        // Generate JWT after successful login
        String token = jwtService.generateToken(
                member.getEmail()
        );

        return new AuthResult(
                token,
                MemberResponse.from(member)
        );
    }

    /**
     * Get member by email.
     */
    public MemberResponse getByEmail(String email) {

        return repository
                .findByEmailIgnoreCase(email)
                .map(MemberResponse::from)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Member not found."
                        )
                );
    }

    private static boolean isBcrypt(String stored) {
        return stored != null && stored.startsWith("$2");
    }

    /** BCrypt hash -> normal check. Legacy plain-text value -> constant-time equality (upgraded on login). */
    private boolean passwordMatches(String raw, String stored) {
        if (raw == null || stored == null) return false;
        if (isBcrypt(stored)) return passwordEncoder.matches(raw, stored);
        return MessageDigest.isEqual(
                raw.getBytes(StandardCharsets.UTF_8),
                stored.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Normalizes mobile number.
     *
     * Example:
     * +91 98765-43210
     * becomes
     * +919876543210
     */
    private static String normalizeMobile(String raw) {

        return raw == null
                ? ""
                : raw
                .trim()
                .replaceAll("[\\s()\\-]", "");
    }

    public record AuthResult(
            String token,
            MemberResponse member
    ) {}
}