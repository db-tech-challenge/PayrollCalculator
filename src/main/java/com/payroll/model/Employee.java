package com.payroll.model;

/**
 * Employee model.
 * Contains employee data.
 */
public class    Employee {
    private String fullName;
    private String location;
    private String employeeId;
    private String taxClass;
    private String atLevel;
    private String status;
    private int daysWorked;
    private String phone;
    private String birthdayDate;
    private String password;

    // Constructors, getters, and setters

    public Employee() {
    }

    public Employee(String fullName, String location, String employeeId, String taxClass,
                    String atLevel, String status, int daysWorked, String phone,
                    String birthdayDate, String password) {
        this.fullName = fullName;
        this.location = location;
        this.employeeId = employeeId;
        this.taxClass = taxClass;
        this.atLevel = atLevel;
        this.status = status;
        this.daysWorked = daysWorked;
        this.phone = phone;
        this.birthdayDate = birthdayDate;
        this.password = password;
    }

    // Getters and setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getTaxClass() {
        return taxClass;
    }

    public void setTaxClass(String taxClass) {
        this.taxClass = taxClass;
    }

    public String getAtLevel() {
        return atLevel;
    }

    public void setAtLevel(String atLevel) {
        this.atLevel = atLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(int daysWorked) {
        this.daysWorked = daysWorked;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthdayDate() {
        return birthdayDate;
    }

    public void setBirthdayDate(String birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Employee{" +
            "fullName='" + fullName + '\'' +
            ", employeeId='" + employeeId + '\'' +
            ", status='" + status + '\'' +
            '}';
    }
}
