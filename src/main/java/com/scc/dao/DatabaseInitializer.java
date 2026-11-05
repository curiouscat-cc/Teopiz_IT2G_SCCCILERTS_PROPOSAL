package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        String staffAccountsTable = "CREATE TABLE IF NOT EXISTS Staff_Accounts ("
                + "acc_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "password TEXT NOT NULL, "
                + "full_name TEXT, "
                + "email TEXT, "
                + "role TEXT NOT NULL, "
                + "department TEXT, "
                + "status TEXT NOT NULL DEFAULT 'PENDING'"
                + ")";

        String labEquipmentTable = "CREATE TABLE IF NOT EXISTS Lab_Equipment ("
                + "equip_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "property_tag_no TEXT NOT NULL UNIQUE, "
                + "item_name TEXT NOT NULL, "
                + "category TEXT NOT NULL, "
                + "lab_room_no TEXT NOT NULL, "
                + "current_status TEXT NOT NULL DEFAULT 'Available'"
                + ")";

        String borrowTransactionsTable = "CREATE TABLE IF NOT EXISTS Borrow_Transactions ("
                + "trans_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "equipment_id INTEGER NOT NULL, "
                + "faculty_id INTEGER NOT NULL, "
                + "processed_by_tech_id INTEGER, "
                + "borrow_date TEXT, "
                + "expected_return_date TEXT, "
                + "actual_return_date TEXT, "
                + "transaction_status TEXT NOT NULL DEFAULT 'Pending', "
                + "FOREIGN KEY (equipment_id) REFERENCES Lab_Equipment(equip_id), "
                + "FOREIGN KEY (faculty_id) REFERENCES Staff_Accounts(acc_id), "
                + "FOREIGN KEY (processed_by_tech_id) REFERENCES Staff_Accounts(acc_id)"
                + ")";

        String maintenanceTicketsTable = "CREATE TABLE IF NOT EXISTS Maintenance_Tickets ("
                + "ticket_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "equipment_id INTEGER NOT NULL, "
                + "reported_by_id INTEGER NOT NULL, "
                + "assigned_tech_id INTEGER, "
                + "issue_description TEXT NOT NULL, "
                + "repair_status TEXT NOT NULL DEFAULT 'Pending', "
                + "date_logged TEXT, "
                + "FOREIGN KEY (equipment_id) REFERENCES Lab_Equipment(equip_id), "
                + "FOREIGN KEY (reported_by_id) REFERENCES Staff_Accounts(acc_id), "
                + "FOREIGN KEY (assigned_tech_id) REFERENCES Staff_Accounts(acc_id)"
                + ")";

        String activityLogsTable = "CREATE TABLE IF NOT EXISTS Activity_Logs ("
                + "log_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "acc_id INTEGER NOT NULL, "
                + "action TEXT NOT NULL, "
                + "details TEXT, "
                + "log_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (acc_id) REFERENCES Staff_Accounts(acc_id)"
                + ")";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(staffAccountsTable);
            statement.execute(labEquipmentTable);
            statement.execute(borrowTransactionsTable);
            statement.execute(maintenanceTicketsTable);
            statement.execute(activityLogsTable);

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        }
    }
}