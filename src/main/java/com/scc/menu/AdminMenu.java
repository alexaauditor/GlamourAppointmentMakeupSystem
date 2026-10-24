package com.scc.menu;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final Scanner sc;
    private final UserDao userDao;

    public AdminMenu(Scanner sc, UserDao userDao) {
        this.sc = sc;
        this.userDao = userDao;
    }

    public void show() {
        boolean running = true;

        while (running) {
            System.out.println("\n===================================");
            System.out.println("       GLAMOURA ADMIN DASHBOARD");
            System.out.println("===================================");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve an Account");
            System.out.println("4. Reject an Account");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    displayUsers(userDao.getAllUsers(),
                            "ALL SYSTEM USERS");
                    break;

                case "2":
                    displayUsers(userDao.getPendingUsers(),
                            "PENDING REGISTRATIONS");
                    break;

                case "3":
                    approveAccount();
                    break;

                case "4":
                    rejectAccount();
                    break;

                case "5":
                    running = false;
                    System.out.println("\nAdmin logged out.");
                    break;

                default:
                    System.out.println("Invalid menu option.");
            }
        }
    }

    private void displayUsers(List<User> users, String title) {
        System.out.println("\n================ " + title + " ================");

        System.out.printf("%-8s %-20s %-20s %-18s %-12s%n",
                "USER ID", "NAME", "USERNAME", "ROLE", "STATUS");

        System.out.println(
                "-------------------------------------------------------------------------------"
        );

        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            for (User user : users) {
                System.out.printf("%-8d %-20s %-20s %-18s %-12s%n",
                        user.id(),
                        shorten(user.name(), 19),
                        shorten(user.username(), 19),
                        shorten(user.role(), 17),
                        shorten(user.status(), 11));
            }
        }

        System.out.println(
                "-------------------------------------------------------------------------------"
        );
    }

    private String shorten(String value, int maxLength) {
        if (value == null) {
            return "";
        }

        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength - 3) + "...";
    }

    private void approveAccount() {
        System.out.println("\n=== APPROVE ACCOUNT ===");

        List<User> pendingUsers = userDao.getPendingUsers();

        displayUsers(pendingUsers, "PENDING REGISTRATIONS");

        if (pendingUsers.isEmpty()) {
            return;
        }

        System.out.print("Enter the ID to approve (0 to cancel): ");
        String input = sc.nextLine().trim();

        try {
            int userId = Integer.parseInt(input);

            if (userId == 0) {
                System.out.println("Approval cancelled.");
                return;
            }

            boolean isPending = pendingUsers.stream()
                    .anyMatch(user -> user.id() == userId);

            if (!isPending) {
                System.out.println(
                        "That ID is not in the pending registrations list."
                );
                return;
            }

            boolean success = userDao.approveUser(userId);

            if (success) {
                System.out.println(
                        "Account ID " + userId + " approved successfully!"
                );
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
        }
    }

    private void rejectAccount() {
        System.out.println("\n=== REJECT ACCOUNT ===");

        List<User> pendingUsers = userDao.getPendingUsers();

        displayUsers(pendingUsers, "PENDING REGISTRATIONS");

        if (pendingUsers.isEmpty()) {
            return;
        }

        System.out.print("Enter the ID to reject (0 to cancel): ");
        String input = sc.nextLine().trim();

        try {
            int userId = Integer.parseInt(input);

            if (userId == 0) {
                System.out.println("Rejection cancelled.");
                return;
            }

            boolean isPending = pendingUsers.stream()
                    .anyMatch(user -> user.id() == userId);

            if (!isPending) {
                System.out.println(
                        "That ID is not in the pending registrations list."
                );
                return;
            }

            boolean success = userDao.updateUserStatus(
                    userId, "REJECTED"
            );

            if (success) {
                System.out.println(
                        "Account ID " + userId + " rejected successfully."
                );
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
        }
    }
}