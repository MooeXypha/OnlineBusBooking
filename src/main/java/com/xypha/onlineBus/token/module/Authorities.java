package com.xypha.onlineBus.token.module;

public class Authorities {
    private String role;
    private ModulePermissions permissions;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public ModulePermissions getPermissions() {
        return permissions;
    }

    public void setPermissions(ModulePermissions permissions) {
        this.permissions = permissions;
    }
}
