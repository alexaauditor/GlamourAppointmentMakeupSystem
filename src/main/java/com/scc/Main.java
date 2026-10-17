package com.scc;

import com.scc.dao.DatabaseConnection;
import com.scc.dao.ServiceDao;
import com.scc.dao.UserDao;
import com.scc.menu.AdminMenu;
import com.scc.menu.CustomerMenu;
import com.scc.menu.MakeupArtistMenu;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final UserDao userDao = new UserDao();
    private static final ServiceDao serviceDao = new ServiceDao();

    public static void main(String[] args) {

        // Ensure database tables exist and seed default services safely
        initializeDatabaseAndServices();

        boolean running = true;

        while (running) {

            System.out.println("\n===================================");
            System.out.println(" GLAMOURA MAKEUP APPOINTMENT SYSTEM");
            System.out.println("===================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {

                case "1":
                    login();
                    break;

                case "2":
                    register();
                    break;

                case "3":
                    running = false;
                    System.out.println(
                            "\nThank you for using Glamoura!"
                    );
                    System.out.println("Goodbye! <3");
                    break;

                default:
                    System.out.println(
                            "Invalid menu option. Please choose 1, 2, or 3."
                    );
            }
        }

        sc.close();
    }

    private static void initializeDatabaseAndServices() {
        String createServicesTable = "CREATE TABLE IF NOT EXISTS Services (" +
                "service_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "service_name TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "description TEXT)";

        String createAppointmentTable = "CREATE TABLE IF NOT EXISTS Appointments (" +
                "appointment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_id INTEGER NOT NULL, " +
                "artist_id INTEGER NOT NULL, " +
                "service_id INTEGER NOT NULL, " +
                "appointment_date TEXT NOT NULL, " +
                "status TEXT NOT NULL DEFAULT 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createServicesTable);
            stmt.execute(createAppointmentTable);

            // Seed default services if table is empty
            serviceDao.addService("Bridal Glam", 150.00, "Full coverage glam makeup with lashes.");
            serviceDao.addService("Everyday Natural", 60.00, "Lightweight, glowing natural makeup look.");
            serviceDao.addService("Special Event / Prom", 95.00, "Bold eye makeup and contouring.");

        } catch (Exception e) {
            // Tables or default records already exist
        }
    }

    private static void login() {

        System.out.println("\n=== LOGIN ACCOUNT ===");

        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println(
                    "Username and password cannot be empty."
            );
            return;
        }

        boolean success = userDao.loginUser(username, password);

        if (!success) {
            return;
        }

        String role = userDao.getUserRole(username);

        if ("ADMIN".equalsIgnoreCase(role)) {

            AdminMenu adminMenu = new AdminMenu(sc, userDao);
            adminMenu.show();

        } else if ("MAKEUP_ARTIST".equalsIgnoreCase(role)) {

            MakeupArtistMenu artistMenu =
                    new MakeupArtistMenu(sc, userDao, username);

            artistMenu.show();

        } else if ("CUSTOMER".equalsIgnoreCase(role)) {

            CustomerMenu customerMenu =
                    new CustomerMenu(sc, userDao, username);

            customerMenu.show();

        } else {

            System.out.println(
                    "Unable to determine your account role."
            );
        }
    }

    private static void register() {

        System.out.println("\n=== REGISTER ACCOUNT ===");

        System.out.print("Enter full name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter new username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.out.println(
                    "Name, username, and password cannot be empty."
            );
            return;
        }

        System.out.println(
                "Select Role [1] MAKEUP ARTIST | [2] CUSTOMER"
        );
        System.out.print("Enter role number: ");

        String roleInput = sc.nextLine().trim();
        String role;

        switch (roleInput) {

            case "1":
                role = "MAKEUP_ARTIST";
                break;

            case "2":
                role = "CUSTOMER";
                break;

            default:
                System.out.println("Invalid role selection.");
                return;
        }

        boolean success =
                userDao.registerUser(name, username, password, role);

        if (success) {
            System.out.println(
                    "Your account has been registered successfully."
            );
            System.out.println("Account status: PENDING");
            System.out.println(
                    "Please wait for administrator approval."
            );
        } else {
            System.out.println(
                    "Registration failed. Please check your details."
            );
        }
    }
}