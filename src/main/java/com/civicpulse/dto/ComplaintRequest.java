package com.civicpulse.dto;

public class ComplaintRequest {
    private String status;
    private String note;
    private String assignedDept;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getAssignedDept() { return assignedDept; }
    public void setAssignedDept(String assignedDept) { this.assignedDept = assignedDept; }
}
