package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class userDao {

    public boolean registerUser(String username, String passwordHash, String fullName, String email, String role, String department) {
        String sql = "INSERT INTO Staff_Accounts (username, password_hash, full_name, email, role, department, acc_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, fullName);
            pstmt.setString(4, email);
            pstmt.setString(5, role.toUpperCase());
            pstmt.setString(6, department);

            pstmt.executeUpdate();
            System.out.println("-> Registration Successful! Account set to PENDING for Custodian approval.");
            return true;

        } catch (SQLException e) {
            System.err.println("Registration Failed: " + e.getMessage());
            return false;
        }
    }

    public String loginUser(String username, String passwordHash) {
        String sql = "SELECT role FROM Staff_Accounts WHERE username = ? AND password_hash = ? AND acc_status = 'ACTIVE'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            }

        } catch (SQLException e) {
            System.err.println("Login Error: " + e.getMessage());
        }

        return null;
    }
}