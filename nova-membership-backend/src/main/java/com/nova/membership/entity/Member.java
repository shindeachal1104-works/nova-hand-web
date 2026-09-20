package com.nova.membership.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_member_email", columnNames = "email"),
           @UniqueConstraint(name = "uk_member_mobile", columnNames = "mobile_number")
       })
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "mobile_number", nullable = false, length = 20)
    private String mobileNumber;

    @Column(nullable = false, length = 180)
    private String email;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(name = "school_college", nullable = false, length = 200)
    private String schoolCollege;

    @Column(nullable = false, length = 80)
    private String className;

    @Column(nullable = false, length = 120)
    private String stream;

    @Column(nullable = false, length = 120)
    private String board;

    @Column(name = "areas_of_interest", nullable = false, length = 500)
    private String areasOfInterest;

    @Column(nullable = false, length = 30)
    private String gender;

    @Column(nullable = false, length = 80)
    private String district;

    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "terms_accepted", nullable = false)
    private boolean termsAccepted;

    @Column(nullable = false, length = 30)
    private String role = "MEMBER";

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate v) { this.dateOfBirth = v; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String v) { this.mobileNumber = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getCity() { return city; }
    public void setCity(String v) { this.city = v; }
    public String getSchoolCollege() { return schoolCollege; }
    public void setSchoolCollege(String v) { this.schoolCollege = v; }
    public String getClassName() { return className; }
    public void setClassName(String v) { this.className = v; }
    public String getStream() { return stream; }
    public void setStream(String v) { this.stream = v; }
    public String getBoard() { return board; }
    public void setBoard(String v) { this.board = v; }
    public String getAreasOfInterest() { return areasOfInterest; }
    public void setAreasOfInterest(String v) { this.areasOfInterest = v; }
    public String getGender() { return gender; }
    public void setGender(String v) { this.gender = v; }
    public String getDistrict() { return district; }
    public void setDistrict(String v) { this.district = v; }
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String v) { this.profilePhotoUrl = v; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String v) { this.passwordHash = v; }
    public boolean isTermsAccepted() { return termsAccepted; }
    public void setTermsAccepted(boolean v) { this.termsAccepted = v; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
