package com.nova.membership.service;


import com.nova.membership.dto.LoginRequest;
import com.nova.membership.dto.MemberResponse;
import com.nova.membership.dto.RegisterRequest;
import com.nova.membership.entity.Member;
import com.nova.membership.repository.MemberRepository;
import com.nova.membership.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository repository;
    private final FileStorageService fileStorageService;
    private final JwtService jwtService;

    public MemberService(
            MemberRepository repository,
            FileStorageService fileStorageService,
            JwtService jwtService
    ) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
        this.jwtService = jwtService;
    }

    /**
     * Registers the member and returns a JWT.
     *
     * NOTE:
     * Password is intentionally stored as plain text here
     * because this is being used for local testing.
     *
     * For production, use BCrypt hashing.
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

        /*
         * Plain-text password.
         *
         * Example:
         * User enters: Achal@123
         * Database stores: Achal@123
         */
        m.setPasswordHash(r.getPassword());

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

        /*
         * Plain-text password comparison.
         *
         * Example:
         * Database: Achal@123
         * Login:    Achal@123
         */
        Member member = found
                .filter(m ->
                        r.password().equals(
                                m.getPasswordHash()
                        )
                )
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