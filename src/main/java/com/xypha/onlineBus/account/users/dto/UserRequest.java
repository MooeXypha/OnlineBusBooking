package com.xypha.onlineBus.account.users.dto;

import com.xypha.onlineBus.account.role.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequest {

    private String username;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$",
            message = "Email must be a valid Gmail address"
    )
    private String gmail;

    @Pattern(
            regexp = "^(09|\\+?959)[0-9]{7,9}$",
            message = "Invalid Myanmar phone number format"
    )
    private String phoneNumber;

    private String nrc;

    private String gender;

    @Past(message = "DOB must be a past date")
    private LocalDate dob;

    private String citizenship;


    private Role role;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
