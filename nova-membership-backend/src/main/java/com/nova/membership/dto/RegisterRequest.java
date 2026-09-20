package com.nova.membership.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * Bound from multipart/form-data via @ModelAttribute.
 *
 * IMPORTANT: @ModelAttribute binding needs SETTERS. The old version only had getters,
 * so every field stayed null/false and registration always failed validation.
 */
public class RegisterRequest {

    @NotBlank(message = "Full name is required.")
    @Size(max = 120, message = "Full name must be 120 characters or fewer.")
    private String fullName;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be in the past.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)   // HTML <input type="date"> sends yyyy-MM-dd
    private LocalDate dateOfBirth;

    @NotBlank(message = "Mobile number is required.")
    @Pattern(regexp = "^\\+?[0-9 ()\\-]{10,20}$", message = "Enter a valid mobile number.")
    private String mobileNumber;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    @Size(max = 180, message = "Email must be 180 characters or fewer.")
    private String email;

    @NotBlank(message = "City is required.")
    @Size(max = 100, message = "City must be 100 characters or fewer.")
    private String city;

    @NotBlank(message = "School / College is required.")
    @Size(max = 200, message = "School / College must be 200 characters or fewer.")
    private String schoolCollege;

    @NotBlank(message = "Class is required.")
    @Size(max = 80, message = "Class must be 80 characters or fewer.")
    private String className;

    @NotBlank(message = "Stream is required.")
    @Size(max = 120, message = "Stream must be 120 characters or fewer.")
    private String stream;

    @NotBlank(message = "Board is required.")
    @Size(max = 120, message = "Board must be 120 characters or fewer.")
    private String board;

    @NotBlank(message = "Please select at least one area of interest.")
    @Size(max = 500, message = "Areas of interest must be 500 characters or fewer.")
    private String areasOfInterest;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 72, message = "Password must be 8 to 72 characters.")
    private String password;

    @NotBlank(message = "Gender is required.")
    @Size(max = 30, message = "Gender must be 30 characters or fewer.")
    private String gender;

    @NotBlank(message = "District is required.")
    @Size(max = 80, message = "District must be 80 characters or fewer.")
    private String district;

    @AssertTrue(message = "You must accept the community guidelines and terms.")
    private boolean termsAccepted;

    private MultipartFile profilePhoto;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getSchoolCollege() { return schoolCollege; }
    public void setSchoolCollege(String schoolCollege) { this.schoolCollege = schoolCollege; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getStream() { return stream; }
    public void setStream(String stream) { this.stream = stream; }
    public String getBoard() { return board; }
    public void setBoard(String board) { this.board = board; }
    public String getAreasOfInterest() { return areasOfInterest; }
    public void setAreasOfInterest(String areasOfInterest) { this.areasOfInterest = areasOfInterest; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public boolean isTermsAccepted() { return termsAccepted; }
    public void setTermsAccepted(boolean termsAccepted) { this.termsAccepted = termsAccepted; }
    public MultipartFile getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(MultipartFile profilePhoto) { this.profilePhoto = profilePhoto; }
}
