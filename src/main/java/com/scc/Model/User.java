package com.scc.Model;

public record User(int accId, String username, String passwordHash, String fullName, String email, String role, String department, String accStatus) {
    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - Role: %s | Dept: %s | Status: %s",
                accId, fullName, username, role, department, accStatus);
    }
}