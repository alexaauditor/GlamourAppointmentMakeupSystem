package com.scc.menu;

import com.scc.dao.UserDao;
import java.util.Scanner;
import com.scc.menu.AdminMenu;
import com.scc.menu.CustomerMenu;

public class Menu {

    private final Scanner sc = new Scanner(System.in);
    private final UserDao userDao = new UserDao();

    public void start() {
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
                    System.out.println("\nThank you for using Glamoura!");
                    System.out.println("Goodbye! <3");
                    break;

                default:
                    System.out.println("Invalid menu option.");
            }
        }

        sc.close();
    }


    private void login() {

        System.out.println("\n=== LOGIN ACCOUNT ===");

        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }

        boolean success = userDao.loginUser(username, password);

        if (success) {
            String role = userDao.getUserRole(username);

            if ("ADMIN".equalsIgnoreCase(role)) {

                AdminMenu adminMenu = new AdminMenu(sc, userDao);
                adminMenu.show();
            }
        }
    }


    private void register() {
        System.out.println("\n=== REGISTER ACCOUNT ===");

        System.out.print("Enter full name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter new username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.out.println("Name, username, and password cannot be empty.");
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
            System.out.println("Registration successful!");
            System.out.println("Account status: PENDING");
        } else {
            System.out.println("Registration failed.");
        }
    }
}