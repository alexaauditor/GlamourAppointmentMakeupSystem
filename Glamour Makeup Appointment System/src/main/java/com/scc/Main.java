
package com.scc;

import com.scc.dao.UserDao;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final UserDao userDao = new UserDao();

    public static void main(String[] args) {

        System.out.println("\n===================================");
        System.out.println(" GLAMOUR MAKEUP APPOINTMENT SYSTEM");
        System.out.println("===================================");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        String choice = sc.nextLine();

        switch (choice) {
            case "1":
                System.out.println("Login will be implemented later.");
                break;

            case "2":
                register();
                break;

            case "3":
                System.out.println("Thank you for using Glamoura!");
                break;

            default:
                System.out.println("Invalid menu option.");
        }

        sc.close();
    }

    private static void register() {

        System.out.println("\n=== REGISTER ACCOUNT ===");

        System.out.print("Enter new username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }

        System.out.println(
                "Select Role [1] ADMIN | [2] MAKEUP ARTIST | [3] CUSTOMER"
        );
        System.out.print("Enter role number: ");

        String roleInput = sc.nextLine().trim();
        String role;

        switch (roleInput) {
            case "1":
                role = "ADMIN";
                break;

            case "2":
                role = "MAKEUP_ARTIST";
                break;

            case "3":
                role = "CUSTOMER";
                break;

            default:
                System.out.println("Invalid role selection.");
                return;
        }

        boolean success =
                userDao.registerUser(username, password, role);

        if (success) {
            System.out.println("Registration successful!");
            System.out.println("Account status: PENDING");
        } else {
            System.out.println(
                    "Registration failed. Please check the database or username."
            );
        }
    }
}
