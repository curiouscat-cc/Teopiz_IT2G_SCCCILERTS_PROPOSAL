package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class userDao {
    public boolean registerUser(String username, String password, String fullName, String email, String role, String department) {
        String sql = "INSERT INTO Staff_Accounts (username, password, full_name, email, role, department, status) VALUES (?, ?, ?, ?, ?, ?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setString(4, email);
            pstmt.setString(5, role);
            pstmt.setString(6, department);
            pstmt.executeUpdate();

            System.out.println("-> Registration Successful! Account status is PENDING approval.");
            return true;

        } catch (SQLException e) {
            System.err.println("Registration Failed: " + e.getMessage());
            return false;
        }
    }

    public String loginUser(String username, String password) {
        // Note: Check status = 'ACTIVE' or handle pending check in UI
        String sql = "SELECT role FROM Staff_Accounts WHERE username = ? AND password = ? AND status = 'ACTIVE'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            }

        } catch (Exception e) {
            System.out.println("Login Error: " + e.getMessage());
        }

        return null;
    }
}