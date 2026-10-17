package com.scc.menu;

import com.scc.dao.UserDao;
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
            System.out.println("1. View Pending Accounts");
            System.out.println("2. Approve an Account");
            System.out.println("3. Reject an Account");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    userDao.viewPendingUsers();
                    break;
                case "2":
                    approveAccount();
                    break;

                case "3":
                    rejectAccount();
                    break;
                case "4":
                    running = false;
                    System.out.println("\nAdmin logged out.");
                    break;
                default:
                    System.out.println("Invalid menu option.");
            }
        }
    }

    private void approveAccount() {

        System.out.println("\n=== APPROVE ACCOUNT ===");

        userDao.viewPendingUsers();

        System.out.print("Enter the ID of the account to approve: ");

        try {
            int userId = Integer.parseInt(sc.nextLine().trim());

            boolean success = userDao.updateUserStatus(
                    userId, "APPROVED"
            );

            if (success) {
                System.out.println("Account approved successfully!");
            } else {
                System.out.println(
                        "Account not found or is no longer pending."
                );
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
        }
    }

    private void rejectAccount() {

        System.out.println("\n=== REJECT ACCOUNT ===");

        userDao.viewPendingUsers();

        System.out.print("Enter the ID of the account to reject: ");

        try {
            int userId = Integer.parseInt(sc.nextLine().trim());

            boolean success = userDao.updateUserStatus(
                    userId, "REJECTED"
            );

            if (success) {
                System.out.println("Account rejected successfully.");
            } else {
                System.out.println(
                        "Account not found or is no longer pending."
                );
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
        }
    }

}