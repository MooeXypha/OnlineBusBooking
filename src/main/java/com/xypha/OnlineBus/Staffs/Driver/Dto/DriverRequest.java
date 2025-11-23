package com.xypha.OnlineBus.Staffs.Driver.Dto;

import jakarta.validation.constraints.NotNull;

public class DriverRequest {

    @NotNull(message = "Driver name cannot be null")
    private String name;

    @NotNull(message = "Phone number cannot be null")
    private String phoneNumber;

    @NotNull(message = "License number cannot be null")
    private String licenseNumber;

    @NotNull(message = "Employee ID cannot be null")
    private String employeeId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}
