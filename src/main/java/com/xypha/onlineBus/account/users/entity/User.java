package com.xypha.onlineBus.account.users.entity;

import com.xypha.onlineBus.account.role.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;


public class User implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String gmail;
    private String phoneNumber;
    private String nrc;
    private String gender;
    private LocalDate dob;
    private String citizenship;
    private Role role;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return Collections.singleton(()->"ROLE_" + this.role.name());

    }
    @Override
    public boolean isAccountNonExpired(){
        return true;
    }
    @Override
    public boolean isAccountNonLocked(){
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }
    @Override
    public boolean isEnabled(){
        return true;
    }




}
