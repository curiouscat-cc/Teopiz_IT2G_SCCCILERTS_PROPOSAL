package com.scc;

import com.scc.dao.userDao;
import java.util.Scanner;

public class Main {
    public static final Scanner sc = new Scanner(System.in);
    private static final userDao userDao = new userDao();

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n==================================================");
            System.out.println(" SCC-CI LAB & EQUIPMENT RESOURCE TRACKING SYSTEM ");
            System.out.println("==================================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\n============ LOGIN ============");
                    System.out.print("Enter username: ");
                    String username = sc.nextLine();
                    System.out.print("Enter password: ");
                    String password = sc.nextLine();

                    String role = userDao.loginUser(username, password);

                    if (role != null) {
                        System.out.println();
                        System.out.println("Login Successful!");
                        System.out.println("role: " + role);

                        if (role.equalsIgnoreCase("ADMIN")) {
                            System.out.println("Welcome, Property Custodian! Access granted to Full Inventory Management.");
                        } else if (role.equalsIgnoreCase("STAFF")) {
                            System.out.println("Welcome, Staff Member! (Faculty / Lab Technician) Access granted to Equipment Services.");
                        } else {
                            System.out.println("Welcome, User!");
                        }
                    } else {
                        System.out.println("Invalid user or password!");
                    }
                    break;

                case 2:
                    System.out.println("\n============ REGISTER ============");
                    System.out.print("Enter Full Name: ");
                    String fullName = sc.nextLine();

                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();

                    System.out.print("Enter Department (e.g., Faculty / Lab Support): ");
                    String department = sc.nextLine();

                    System.out.print("Enter new username: ");
                    String user = sc.nextLine();

                    System.out.print("Enter new password: ");
                    String pass = sc.nextLine();

                    System.out.println( "Select Role [1] ADMIN [2] STAFF: ");
                    int roleChoice = sc.nextInt();
                    sc.nextLine();

                    String registerRole;
                    if (roleChoice == 1) {
                        registerRole = "ADMIN";
                    } else if (roleChoice == 2) {
                        registerRole = "STAFF";
                    } else {
                        registerRole = null;
                    }

                    if (registerRole != null) {
                        userDao.registerUser(user, pass, fullName, email, registerRole, department);
                    } else {
                        System.out.println("Invalid role selection!");
                    }
                    break;

                case 3:
                    System.out.println("\nThank you for using SCC-CI Lab & Equipment Resource Tracking System!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}