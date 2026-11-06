package com.scc.ui;

import com.scc.dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class LabTechnicianMenu {

    public static void showMenu(Scanner sc, String techUsername) {
        boolean back = false;

        while (!back) {
            System.out.println("\n=======================================");
            System.out.println("          TECHNICIAN DASHBOARD         ");
            System.out.println("=======================================");
            System.out.println("1. Process Checkout / Hand Over Equipment");
            System.out.println("2. Process Return / Check Equipment Tag");
            System.out.println("3. View & Update Maintenance Tickets");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    processCheckout(sc);
                    break;
                case "2":
                    processReturn(sc);
                    break;
                case "3":
                    manageMaintenanceTickets(sc);
                    break;
                case "4":
                    back = true;
                    System.out.println("Logging out from Technician dashboard...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void processCheckout(Scanner sc) {
        System.out.println("\n==================================================================================================");
        System.out.println("                          APPROVED RESERVATIONS READY FOR CHECKOUT                                ");
        System.out.println("==================================================================================================");
        String sql = "SELECT b.transaction_id, e.equipment_name, b.username, b.borrow_date FROM Borrow_Transactions b JOIN Lab_Equipment e ON b.equipment_id = e.equipment_id WHERE b.status = 'APPROVED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.printf("%-6s | %-25s | %-18s | %-12s%n", "TX ID", "EQUIPMENT NAME", "BORROWER", "DATE");
            System.out.println("--------------------------------------------------------------------------------------------------");
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                System.out.printf("%-6d | %-25s | %-18s | %-12s%n",
                        rs.getInt("transaction_id"),
                        truncate(rs.getString("equipment_name"), 25),
                        truncate(rs.getString("username"), 18),
                        rs.getString("borrow_date")
                );
            }
            if (!hasRows) {
                System.out.println(" No approved reservations awaiting checkout.");
                System.out.println("==================================================================================================");
                return;
            }
            System.out.println("==================================================================================================");

            int txId = Authentication.readIntInput(sc, "Enter Transaction ID to handover (0 to cancel): ");
            if (txId == 0) return;

            String updateSql = "UPDATE Borrow_Transactions SET status = 'CHECKED_OUT' WHERE transaction_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, txId);
                updateStmt.executeUpdate();
                System.out.println("-> Success! Equipment checked out to borrower.");
            }
        } catch (SQLException e) {
            System.err.println("Error during checkout: " + e.getMessage());
        }
    }

    private static void processReturn(Scanner sc) {
        System.out.println("\n==================================================================================================");
        System.out.println("                           CHECKED OUT EQUIPMENT AWAITING RETURN                                  ");
        System.out.println("==================================================================================================");
        String sql = "SELECT b.transaction_id, e.equipment_name, b.username, b.borrow_date FROM Borrow_Transactions b JOIN Lab_Equipment e ON b.equipment_id = e.equipment_id WHERE b.status = 'CHECKED_OUT'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.printf("%-6s | %-25s | %-18s | %-12s%n", "TX ID", "EQUIPMENT NAME", "BORROWER", "BORROW DATE");
            System.out.println("--------------------------------------------------------------------------------------------------");
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                System.out.printf("%-6d | %-25s | %-18s | %-12s%n",
                        rs.getInt("transaction_id"),
                        truncate(rs.getString("equipment_name"), 25),
                        truncate(rs.getString("username"), 18),
                        rs.getString("borrow_date")
                );
            }
            if (!hasRows) {
                System.out.println(" No items currently checked out.");
                System.out.println("==================================================================================================");
                return;
            }
            System.out.println("==================================================================================================");

            int txId = Authentication.readIntInput(sc, "Enter Transaction ID to process return & verify tag (0 to cancel): ");
            if (txId == 0) return;

            String returnDate = LocalDate.now().toString();
            String updateSql = "UPDATE Borrow_Transactions SET status = 'RETURNED', return_date = ? WHERE transaction_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, returnDate);
                updateStmt.setInt(2, txId);
                updateStmt.executeUpdate();
                System.out.println("-> Success! Equipment return processed and tag verified.");
            }
        } catch (SQLException e) {
            System.err.println("Error processing return: " + e.getMessage());
        }
    }

    private static void manageMaintenanceTickets(Scanner sc) {
        System.out.println("\n==================================================================================================");
        System.out.println("                                  MAINTENANCE TICKETS DIRECTORY                                   ");
        System.out.println("==================================================================================================");
        String sql = "SELECT m.ticket_id, e.equipment_name, m.reported_by, m.issue_description, m.status FROM Maintenance_Tickets m JOIN Lab_Equipment e ON m.equipment_id = e.equipment_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.printf("%-6s | %-20s | %-15s | %-25s | %-12s%n", "TID", "EQUIPMENT", "REPORTED BY", "ISSUE DETAILS", "STATUS");
            System.out.println("--------------------------------------------------------------------------------------------------");
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                System.out.printf("%-6d | %-20s | %-15s | %-25s | %-12s%n",
                        rs.getInt("ticket_id"),
                        truncate(rs.getString("equipment_name"), 20),
                        truncate(rs.getString("reported_by"), 15),
                        truncate(rs.getString("issue_description"), 25),
                        rs.getString("status")
                );
            }
            if (!hasRows) {
                System.out.println(" No maintenance tickets found.");
                System.out.println("==================================================================================================");
                return;
            }
            System.out.println("==================================================================================================");

            int ticketId = Authentication.readIntInput(sc, "Enter Ticket ID to update status (0 to cancel): ");
            if (ticketId == 0) return;

            System.out.print("Enter New Status (IN_PROGRESS, RESOLVED, CONDEMNED): ");
            String status = sc.nextLine().toUpperCase();

            String updateSql = "UPDATE Maintenance_Tickets SET status = ? WHERE ticket_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, status);
                updateStmt.setInt(2, ticketId);
                updateStmt.executeUpdate();
                System.out.println("-> Success! Maintenance ticket status updated.");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String truncate(String value, int maxLength) {
        if (value == null) return "";
        if (value.length() <= maxLength) return value;
        return value.substring(0, maxLength - 3) + "...";
    }
}