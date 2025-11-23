package com.xypha.OnlineBus.Staffs.Assistant.Entity;


import jakarta.validation.constraints.NotNull;


public class Assistant {

    private Long id;

    @NotNull(message = "Assistant name cannot be null")
    private String name;

    @NotNull(message = "Phone number cannot be null")
    private String phoneNumber;


    @NotNull(message = "Employee ID cannot be null")
    private String employeeId;

    public Assistant(){

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

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}
