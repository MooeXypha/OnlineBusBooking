package com.xypha.onlineBus.staffs.Assistant.Dto;

public class AssistantResponse {

    private Long id;
    private String name;
    private String phoneNumber;
    private String employeeId;

    public AssistantResponse(Long id, String name, String phoneNumber, String employeeId) {
    }

    public AssistantResponse() {

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
