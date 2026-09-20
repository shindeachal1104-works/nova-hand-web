package com.nova.membership.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "volunteer_applications",
        uniqueConstraints = @UniqueConstraint(name = "uk_volunteer_mobile", columnNames = "mobile"))
public class VolunteerApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    /** Normalised mobile number (digits and an optional leading +). */
    @Column(nullable = false, length = 20)
    private String mobile;

    private Integer age;

    @Column(length = 100)
    private String city;

    @Column(length = 200)
    private String occupation;

    @Column(length = 500)
    private String interests;

    @Column(length = 1000)
    private String reason;

    @Column(length = 80)
    private String availability;

    @Column(name = "volunteer_type", length = 80)
    private String volunteerType;

    /** "form" or "share-5". */
    @Column(length = 40)
    private String source;

    /** Mobile number (last 10 digits) of the person whose link brought this volunteer. */
    @Column(name = "referred_by", length = 20)
    private String referredBy;

    /** PENDING, APPROVED or REJECTED. */
    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getMobile() { return mobile; }
    public void setMobile(String v) { this.mobile = v; }
    public Integer getAge() { return age; }
    public void setAge(Integer v) { this.age = v; }
    public String getCity() { return city; }
    public void setCity(String v) { this.city = v; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String v) { this.occupation = v; }
    public String getInterests() { return interests; }
    public void setInterests(String v) { this.interests = v; }
    public String getReason() { return reason; }
    public void setReason(String v) { this.reason = v; }
    public String getAvailability() { return availability; }
    public void setAvailability(String v) { this.availability = v; }
    public String getVolunteerType() { return volunteerType; }
    public void setVolunteerType(String v) { this.volunteerType = v; }
    public String getSource() { return source; }
    public void setSource(String v) { this.source = v; }
    public String getReferredBy() { return referredBy; }
    public void setReferredBy(String v) { this.referredBy = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}