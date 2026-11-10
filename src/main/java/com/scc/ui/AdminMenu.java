package com.scc.ui;

import com.scc.Model.User;
import com.scc.dao.DatabaseConnection;
import com.scc.dao.userDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    public static void showMenu(Scanner sc, userDao userDao, User currentAdmin) {
        boolean back = false;

        while (!back) {
            System.out.println("\n=======================================");
            System.out.println("            ADMIN DASHBOARD            ");
            System.out.println("=======================================");
            System.out.println("1. Manage Account Registrations");
            System.out.println("2. Manage Lab Equipment");
            System.out.println("3. Manage Pending Equipment Reservations");
            System.out.println("4. View Borrow History");
            System.out.println("5. View Monthly Utilization Reports");
            System.out.println("6. Logout");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    manageAccountRegistrationsMenu(sc, userDao, currentAdmin);
                    break;
                case "2":
                    manageLabEquipmentMenu(sc);
                    break;
                case "3":
                    managePendingReservations(sc);
                    break;
                case "4":
                    viewBorrowHistory();
                    break;
                case "5":
                    viewMonthlyUtilizationReports(sc);
                    break;
                case "6":
                    back = true;
                    System.out.println("Logging out from Admin dashboard...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void manageAccountRegistrationsMenu(Scanner sc, userDao userDao, User currentAdmin) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=======================================");
            System.out.println("     MANAGE ACCOUNT REGISTRATIONS      ");
            System.out.println("=======================================");
            System.out.println("1. Add New Account");
            System.out.println("2. View All Accounts");
            System.out.println("3. Update Account Role / Status");
            System.out.println("4. Delete Account");
            System.out.println("5. Back to Admin Dashboard");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("Enter Username: ");
                    String uname = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String pass = sc.nextLine();
                    System.out.print("Enter Full Name: ");
                    String fname = sc.nextLine();
                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();
                    System.out.print("Enter Role (ADMIN, FACULTY, TECHNICIAN): ");
                    String role = sc.nextLine().toUpperCase();
                    System.out.print("Enter Department: ");
                    String dept = sc.nextLine();

                    String insertSql = "INSERT INTO Staff_Accounts (username, password, full_name, email, role, department, status) VALUES (?, ?, ?, ?, ?, ?, 'PENDING')";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                        pstmt.setString(1, uname);
                        pstmt.setString(2, pass);
                        pstmt.setString(3, fname);
                        pstmt.setString(4, email);
                        pstmt.setString(5, role);
                        pstmt.setString(6, dept);
                        pstmt.executeUpdate();
                        System.out.println("-> Success! Account registered with PENDING status.");
                    } catch (SQLException e) {
                        System.err.println("Error adding account: " + e.getMessage());
                    }
                    break;
                case "2":
                    printStaffAccountsTable(userDao.getAllUsers(), "ALL SYSTEM ACCOUNTS");
                    break;
                case "3":
                    printStaffAccountsTable(userDao.getAllUsers(), "ALL SYSTEM ACCOUNTS");
                    int targetId = Authentication.readIntInput(sc, "Enter User ID to Update (0 to cancel): ");
                    if (targetId == 0) break;

                    if (targetId == currentAdmin.id()) {
                        System.out.println("WARNING: Cannot modify your own active admin account.");
                        break;
                    }

                    System.out.print("Enter New Role (ADMIN, FACULTY, TECHNICIAN): ");
                    String newRole = sc.nextLine().toUpperCase();
                    System.out.print("Enter New Status (PENDING, APPROVED, ACTIVE): ");
                    String newStatus = sc.nextLine().toUpperCase();

                    if (userDao.updateUserRoleAndStatus(targetId, newRole, newStatus)) {
                        System.out.println("-> Success! Account updated.");
                    } else {
                        System.out.println("-> Failed to update account.");
                    }
                    break;
                case "4":
                    printStaffAccountsTable(userDao.getAllUsers(), "ALL SYSTEM ACCOUNTS");
                    int delId = Authentication.readIntInput(sc, "Enter User ID to Delete (0 to cancel): ");
                    if (delId == 0) break;

                    if (delId == currentAdmin.id()) {
                        System.out.println("WARNING: Cannot delete your own active admin account.");
                        break;
                    }

                    String delSql = "DELETE FROM Staff_Accounts WHERE acc_id = ?";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(delSql)) {
                        pstmt.setInt(1, delId);
                        int rows = pstmt.executeUpdate();
                        if (rows > 0) System.out.println("-> Success! Account deleted.");
                        else System.out.println("-> Account ID not found.");
                    } catch (SQLException e) {
                        System.err.println("Error deleting account: " + e.getMessage());
                    }
                    break;
                case "5":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void manageLabEquipmentMenu(Scanner sc) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=======================================");
            System.out.println("        MANAGE LAB EQUIPMENT           ");
            System.out.println("=======================================");
            System.out.println("1. Add Equipment");
            System.out.println("2. View Equipment Inventory");
            System.out.println("3. Update Equipment Details/Status");
            System.out.println("4. Delete Equipment");
            System.out.println("5. Back to Admin Dashboard");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("Enter Equipment Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Category: ");
                    String category = sc.nextLine();
                    System.out.print("Enter Serial Number: ");
                    String serial = sc.nextLine();

                    String insertSql = "INSERT INTO Lab_Equipment (equipment_name, category, serial_number, status) VALUES (?, ?, ?, 'AVAILABLE')";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                        pstmt.setString(1, name);
                        pstmt.setString(2, category);
                        pstmt.setString(3, serial);
                        pstmt.executeUpdate();
                        System.out.println("-> Success! Equipment added to inventory.");
                    } catch (SQLException e) {
                        System.err.println("Error adding equipment: " + e.getMessage());
                    }
                    break;
                case "2":
                    viewEquipmentInventory();
                    break;
                case "3":
                    viewEquipmentInventory();
                    int eqId = Authentication.readIntInput(sc, "Enter Equipment ID to update: ");
                    if (eqId == 0) break;

                    System.out.print("Enter New Equipment Name: ");
                    String newName = sc.nextLine();
                    System.out.print("Enter New Category: ");
                    String newCat = sc.nextLine();
                    System.out.print("Enter New Serial Number: ");
                    String newSerial = sc.nextLine();
                    System.out.print("Enter Status (AVAILABLE, MAINTENANCE, RETIRED): ");
                    String newStat = sc.nextLine().toUpperCase();

                    String updateSql = "UPDATE Lab_Equipment SET equipment_name = ?, category = ?, serial_number = ?, status = ? WHERE equipment_id = ?";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                        pstmt.setString(1, newName);
                        pstmt.setString(2, newCat);
                        pstmt.setString(3, newSerial);
                        pstmt.setString(4, newStat);
                        pstmt.setInt(5, eqId);
                        pstmt.executeUpdate();
                        System.out.println("-> Success! Equipment record updated.");
                    } catch (SQLException e) {
                        System.err.println("Error updating equipment: " + e.getMessage());
                    }
                    break;
                case "4":
                    viewEquipmentInventory();
                    int delId = Authentication.readIntInput(sc, "Enter Equipment ID to delete (0 to cancel): ");
                    if (delId == 0) break;

                    String delSql = "DELETE FROM Lab_Equipment WHERE equipment_id = ?";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(delSql)) {
                        pstmt.setInt(1, delId);
                        int rows = pstmt.executeUpdate();
                        if (rows > 0) System.out.println("-> Success! Equipment deleted.");
                        else System.out.println("-> Equipment ID not found.");
                    } catch (SQLException e) {
                        System.err.println("Error deleting equipment: " + e.getMessage());
                    }
                    break;
                case "5":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void viewEquipmentInventory() {
        System.out.println("\n==================================================================================================");
        System.out.println("                                 COMPLETE EQUIPMENT INVENTORY                                     ");
        System.out.println("==================================================================================================");
        String sql = "SELECT equipment_id, equipment_name, category, serial_number, status FROM Lab_Equipment";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.printf("%-6s | %-25s | %-20s | %-18s | %-12s%n", "ID", "EQUIPMENT NAME", "CATEGORY", "SERIAL NO.", "STATUS");
            System.out.println("--------------------------------------------------------------------------------------------------");
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                System.out.printf("%-6d | %-25s | %-20s | %-18s | %-12s%n",
                        rs.getInt("equipment_id"),
                        truncate(rs.getString("equipment_name"), 25),
                        truncate(rs.getString("category"), 20),
                        truncate(rs.getString("serial_number"), 18),
                        rs.getString("status")
                );
            }
            if (!hasRows) System.out.println(" No equipment found in inventory.");
            System.out.println("==================================================================================================");
        } catch (SQLException e) {
            System.err.println("Error fetching inventory: " + e.getMessage());
        }
    }

    private static void managePendingReservations(Scanner sc) {
        System.out.println("\n==================================================================================================");
        System.out.println("                                PENDING EQUIPMENT RESERVATIONS                                    ");
        System.out.println("==================================================================================================");

        String sql = "SELECT b.transaction_id, e.equipment_name, b.username, b.borrow_date, b.status " +
                "FROM Borrow_Transactions b JOIN Lab_Equipment e ON b.equipment_id = e.equipment_id " +
                "WHERE b.status = 'PENDING'";

        List<Integer> pendingIds = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.printf("%-6s | %-25s | %-18s | %-15s | %-10s%n", "TX ID", "EQUIPMENT NAME", "REQUESTED BY", "DATE", "STATUS");
            System.out.println("--------------------------------------------------------------------------------------------------");
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                int txId = rs.getInt("transaction_id");
                pendingIds.add(txId);
                System.out.printf("%-6d | %-25s | %-18s | %-15s | %-10s%n",
                        txId,
                        truncate(rs.getString("equipment_name"), 25),
                        truncate(rs.getString("username"), 18),
                        rs.getString("borrow_date"),
                        rs.getString("status")
                );
            }
            if (!hasRows) {
                System.out.println(" No pending reservations found.");
                System.out.println("==================================================================================================");
                return;
            }
            System.out.println("==================================================================================================");
        } catch (SQLException e) {
            System.err.println("Error fetching reservations: " + e.getMessage());
            return;
        }

        int txId = Authentication.readIntInput(sc, "Enter Transaction ID to Approve (0 to cancel): ");
        if (txId == 0) return;

        if (!pendingIds.contains(txId)) {
            System.out.println("-> Error: Invalid Transaction ID or it is not currently pending.");
            return;
        }

        String updateSql = "UPDATE Borrow_Transactions SET status = 'APPROVED' WHERE transaction_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setInt(1, txId);
            int rowsUpdated = updateStmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("-> Success! Reservation approved.");
            } else {
                System.out.println("-> Failed to approve reservation.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
        }
    }

    private static void viewBorrowHistory() {
        System.out.println("\n==================================================================================================");
        System.out.println("                                  ALL BORROW TRANSACTIONS HISTORY                                 ");
        System.out.println("==================================================================================================");

        String sql = "SELECT b.transaction_id, e.equipment_name, b.username, b.borrow_date, b.return_date, b.status " +
                "FROM Borrow_Transactions b JOIN Lab_Equipment e ON b.equipment_id = e.equipment_id " +
                "ORDER BY b.transaction_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.printf("%-6s | %-25s | %-18s | %-12s | %-12s%n", "TX ID", "EQUIPMENT", "BORROWER", "DATE", "STATUS");
            System.out.println("--------------------------------------------------------------------------------------------------");
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                System.out.printf("%-6d | %-25s | %-18s | %-12s | %-12s%n",
                        rs.getInt("transaction_id"),
                        truncate(rs.getString("equipment_name"), 25),
                        truncate(rs.getString("username"), 18),
                        rs.getString("borrow_date"),
                        rs.getString("status")
                );
            }
            if (!hasRows) System.out.println(" No transaction records found.");
            System.out.println("==================================================================================================");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void viewMonthlyUtilizationReports(Scanner sc) {
        System.out.println("\n==========================================================================");
        System.out.println("                        MONTHLY UTILIZATION REPORT                        ");
        System.out.println("==========================================================================");

        String sql = "SELECT SUBSTR(borrow_date, 1, 7) AS month, COUNT(*) AS total_transactions " +
                "FROM Borrow_Transactions GROUP BY month ORDER BY month DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.printf("%-20s | %-25s%n", "MONTH (YYYY-MM)", "TOTAL TRANSACTIONS");
            System.out.println("--------------------------------------------------------------------------");
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                System.out.printf("%-20s | %-25d%n", rs.getString("month"), rs.getInt("total_transactions"));
            }
            if (!hasRows) System.out.println(" No utilization data recorded.");
            System.out.println("==========================================================================");
        } catch (SQLException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }

    private static void printStaffAccountsTable(List<User> users, String title) {
        System.out.println("\n==================================================================================================");
        System.out.println(" " + title);
        System.out.println("==================================================================================================");
        if (users.isEmpty()) {
            System.out.println(" No records found.");
            System.out.println("==================================================================================================");
            return;
        }
        System.out.printf("%-6s | %-20s | %-15s | %-12s%n", "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println("--------------------------------------------------------------------------------------------------");
        for (User u : users) {
            System.out.printf("%-6d | %-20s | %-15s | %-12s%n",
                    u.id(),
                    truncate(u.username(), 20),
                    truncate(u.role(), 15),
                    u.status()
            );
        }
        System.out.println("==================================================================================================");
    }

    private static String truncate(String value, int maxLength) {
        if (value == null) return "";
        if (value.length() <= maxLength) return value;
        return value.substring(0, maxLength - 3) + "...";
    }
}