package com.scc.ui;

import com.scc.dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class FacultyMemberMenu {

    public static void showMenu(Scanner sc, String facultyUsername) {
        boolean back = false;

        while (!back) {
            System.out.println("\n=======================================");
            System.out.println("          FACULTY DASHBOARD            ");
            System.out.println("=======================================");
            System.out.println("1. Reserve/Borrow Equipment");
            System.out.println("2. View Borrow History");
            System.out.println("3. Report Damaged Equipment");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    reserveEquipment(sc, facultyUsername);
                    break;
                case "2":
                    viewBorrowHistory(facultyUsername);
                    break;
                case "3":
                    reportDamagedEquipment(sc, facultyUsername);
                    break;
                case "4":
                    back = true;
                    System.out.println("Logging out from Faculty dashboard...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void reserveEquipment(Scanner sc, String facultyUsername) {
        viewAvailableEquipment();
        int equipmentId = readIntInput(sc, "Enter Equipment ID to reserve (0 to cancel): ");
        if (equipmentId == 0) return;

        String borrowDate = LocalDate.now().toString();
        String sql = "INSERT INTO Borrow_Transactions (equipment_id, username, borrow_date, status) VALUES (?, ?, ?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, equipmentId);
            pstmt.setString(2, facultyUsername);
            pstmt.setString(3, borrowDate);
            pstmt.executeUpdate();
            System.out.println("-> Success! Equipment reservation submitted as PENDING.");
        } catch (SQLException e) {
            System.err.println("Error reserving equipment: " + e.getMessage());
        }
    }

    private static void viewAvailableEquipment() {
        System.out.println("\n==================================================================================================");
        System.out.println("                                AVAILABLE LAB EQUIPMENT CATALOG                                   ");
        System.out.println("==================================================================================================");
        String sql = "SELECT equipment_id, equipment_name, category, serial_number FROM Lab_Equipment WHERE status = 'AVAILABLE'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.printf("%-6s | %-25s | %-20s | %-18s%n", "ID", "EQUIPMENT NAME", "CATEGORY", "SERIAL NO.");
            System.out.println("--------------------------------------------------------------------------------------------------");
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                System.out.printf("%-6d | %-25s | %-20s | %-18s%n",
                        rs.getInt("equipment_id"),
                        truncate(rs.getString("equipment_name"), 25),
                        truncate(rs.getString("category"), 20),
                        truncate(rs.getString("serial_number"), 18)
                );
            }
            if (!hasRows) System.out.println(" No available equipment found.");
            System.out.println("==================================================================================================");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void viewBorrowHistory(String facultyUsername) {
        System.out.println("\n==================================================================================================");
        System.out.println("                                MY BORROW / RESERVATION HISTORY                                   ");
        System.out.println("==================================================================================================");

        String sql = "SELECT b.transaction_id, e.equipment_name, b.borrow_date, b.return_date, b.status " +
                "FROM Borrow_Transactions b JOIN Lab_Equipment e ON b.equipment_id = e.equipment_id " +
                "WHERE b.username = ? ORDER BY b.transaction_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, facultyUsername);
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.printf("%-6s | %-25s | %-12s | %-12s | %-12s%n", "TX ID", "EQUIPMENT NAME", "BORROW DATE", "RETURN DATE", "STATUS");
                System.out.println("--------------------------------------------------------------------------------------------------");
                boolean hasRows = false;
                while (rs.next()) {
                    hasRows = true;
                    System.out.printf("%-6d | %-25s | %-12s | %-12s | %-12s%n",
                            rs.getInt("transaction_id"),
                            truncate(rs.getString("equipment_name"), 25),
                            rs.getString("borrow_date"),
                            (rs.getString("return_date") != null ? rs.getString("return_date") : "N/A"),
                            rs.getString("status")
                    );
                }
                if (!hasRows) System.out.println(" No transaction records found.");
                System.out.println("==================================================================================================");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void reportDamagedEquipment(Scanner sc, String facultyUsername) {
        viewAvailableEquipment();
        int eqId = readIntInput(sc, "Enter Equipment ID experiencing damage (0 to cancel): ");
        if (eqId == 0) return;

        System.out.print("Describe the damage/issue details: ");
        String issue = sc.nextLine();
        String date = LocalDate.now().toString();

        String sql = "INSERT INTO Maintenance_Tickets (equipment_id, reported_by, issue_description, report_date, status) VALUES (?, ?, ?, ?, 'OPEN')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eqId);
            pstmt.setString(2, facultyUsername);
            pstmt.setString(3, issue);
            pstmt.setString(4, date);
            pstmt.executeUpdate();
            System.out.println("-> Success! Maintenance report logged for technician review.");
        } catch (SQLException e) {
            System.err.println("Error reporting damage: " + e.getMessage());
        }
    }

    // Input safeguard helper to safely read integers without crashing
    private static int readIntInput(Scanner sc, String prompt) {
        System.out.print(prompt);
        while (true) {
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }

    private static String truncate(String value, int maxLength) {
        if (value == null) return "";
        if (value.length() <= maxLength) return value;
        return value.substring(0, maxLength - 3) + "...";
    }
}