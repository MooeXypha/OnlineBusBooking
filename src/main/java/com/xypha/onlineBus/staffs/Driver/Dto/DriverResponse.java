package com.xypha.onlineBus.staffs.Driver.Dto;

public class DriverResponse {

    private Long id;
    private String name;
    private String phoneNumber;
    private String licenseNumber;
    private String employeeId;

    public DriverResponse(Long id, String name, String phoneNumber, String licenseNumber, String employeeId) {
    }

    public DriverResponse() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
