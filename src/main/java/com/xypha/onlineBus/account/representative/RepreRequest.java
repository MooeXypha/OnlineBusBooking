package com.xypha.onlineBus.account.representative;

import com.xypha.onlineBus.account.role.Role;
import jakarta.validation.constraints.NotBlank;
import software.amazon.awssdk.annotations.NotNull;

public class RepreRequest {

    @NotBlank(message = "Name cannot be null")
    private String username;

    @NotBlank(message = "Password cannot be null")
    private String password;

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
