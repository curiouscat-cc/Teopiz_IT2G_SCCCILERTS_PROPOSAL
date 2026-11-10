package com.scc.ui;

import com.scc.dao.userDao;
import java.util.Scanner;

public class Authentication {
    public static final Scanner sc = new Scanner(System.in);
    private static final userDao userDao = new userDao();

    public static void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n==================================================");
            System.out.println(" SCC-CI LAB & EQUIPMENT RESOURCE TRACKING SYSTEM  ");
            System.out.println("==================================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            if (!sc.hasNextLine()) break;
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n============ LOGIN ============");
                    System.out.print("Enter username: ");
                    String username = sc.nextLine();
                    System.out.print("Enter password: ");
                    String password = sc.nextLine();

                    String role = userDao.loginUser(username, password);

                    if (role != null) {
                        System.out.println("\nLogin Successful!");
                        System.out.println("Role: " + role);

                        switch (role) {
                            case "Admin":
                                System.out.println("Welcome, Admin!");
                                break;
                            case "Faculty Member":
                                System.out.println("Welcome, Faculty Member!");
                                break;
                            case "Lab Technician":
                                System.out.println("Welcome, Lab Technician!");
                                break;
                            default:
                                System.out.println("Welcome!");
                        }
                    } else {
                        System.out.println("Invalid username, password, or account status is pending approval!");
                    }
                    break;

                case "2":
                    System.out.println("\n========== REGISTRATION ==========");
                    System.out.print("Enter new username: ");
                    String user = sc.nextLine();
                    System.out.print("Enter new password: ");
                    String pass = sc.nextLine();
                    System.out.print("Enter Full Name: ");
                    String fullName = sc.nextLine();
                    System.out.print("Enter Email Address: ");
                    String email = sc.nextLine();

                    System.out.println("Select Role:");
                    System.out.println("[1] Admin");
                    System.out.println("[2] Faculty Member");
                    System.out.println("[3] Lab Technician");
                    System.out.print("Choose role option: ");

                    int roleChoice;
                    try {
                        roleChoice = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        roleChoice = -1;
                    }

                    String registerRole;
                    switch (roleChoice) {
                        case 1:
                            registerRole = "Admin";
                            break;
                        case 2:
                            registerRole = "Faculty Member";
                            break;
                        case 3:
                            registerRole = "Lab Technician";
                            break;
                        default:
                            registerRole = null;
                    }

                    System.out.print("Enter Department: ");
                    String department = sc.nextLine();

                    if (registerRole != null) {
                        String status = "PENDING";
                        userDao.registerUser(user, pass, fullName, email, registerRole, department);
                    } else {
                        System.out.println("Invalid role selection!");
                    }
                    break;

                case "3":
                    System.out.println("Exiting application. Thank you and Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
    public static int readIntInput(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}
