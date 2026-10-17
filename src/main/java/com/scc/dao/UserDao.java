package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class UserDao {

    public boolean registerUser(
            String name, String username, String password, String role) {

        String normalizedRole = role.toUpperCase(Locale.ROOT);

        if (!normalizedRole.equals("CUSTOMER")
                && !normalizedRole.equals("MAKEUP_ARTIST")) {
            System.out.println("Invalid registration role.");
            return false;
        }

        String sql = "INSERT INTO Users " +
                "(name, username, password, role, status) " +
                "VALUES (?, ?, ?, ?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, normalizedRole);

            pstmt.executeUpdate();

            System.out.println("Registration successful!");
            System.out.println("Account status: PENDING");

            return true;

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }


    public boolean loginUser(String username, String password) {

        String sql = "SELECT username, role, status FROM Users " +
                "WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.next()) {
                    System.out.println("Invalid username or password.");
                    return false;
                }

                String status = rs.getString("status");

                if (!"APPROVED".equalsIgnoreCase(status)) {
                    System.out.println(
                            "Your account is not approved yet."
                    );
                    return false;
                }

                System.out.println("\nLogin successful!");
                System.out.println("Welcome, " + username + "!");
                System.out.println("Role: " + rs.getString("role"));

                return true;
            }

        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
            return false;
        }
    }


    public String getUserRole(String username) {

        String sql = "SELECT role FROM Users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error retrieving user role: " + e.getMessage()
            );
        }

        return "";
    }


    public void viewPendingUsers() {

        String sql = "SELECT user_id, username, role, status " +
                "FROM Users " +
                "WHERE status = 'PENDING' " +
                "AND role != 'ADMIN' " +
                "ORDER BY user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n========== PENDING ACCOUNTS ==========");
            System.out.printf("%-10s %-20s %-18s %-12s%n",
                    "USER ID", "USERNAME", "ROLE", "STATUS");
            System.out.println(
                    "------------------------------------------------------------"
            );

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.printf("%-10d %-20s %-18s %-12s%n",
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("status"));
            }

            if (!found) {
                System.out.println("No pending accounts found.");
            }

            System.out.println(
                    "======================================"
            );

        } catch (SQLException e) {
            System.out.println(
                    "Error retrieving pending accounts: " + e.getMessage()
            );
        }
    }


    public boolean updateUserStatus(int userId, String newStatus) {

        String normalizedStatus =
                newStatus.toUpperCase(Locale.ROOT);

        if (!normalizedStatus.equals("APPROVED")
                && !normalizedStatus.equals("REJECTED")) {
            System.out.println("Invalid account status.");
            return false;
        }

        String sql = "UPDATE Users SET status = ? " +
                "WHERE user_id = ? " +
                "AND role != 'ADMIN' AND status = 'PENDING'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, normalizedStatus);
            pstmt.setInt(2, userId);

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println(
                        "Account " + userId + " was " +
                                normalizedStatus.toLowerCase(Locale.ROOT) +
                                "."
                );
                return true;
            }

            System.out.println(
                    "No matching pending account found."
            );
            return false;

        } catch (SQLException e) {
            System.out.println(
                    "Error updating account: " + e.getMessage()
            );
            return false;
        }
    }


    public void viewAllUsers() {
        String sql = "SELECT user_id, name, username, role, status FROM Users ORDER BY user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n========== ALL SYSTEM USERS ==========");
            System.out.printf("%-10s %-20s %-20s %-18s %-12s%n",
                    "USER ID", "NAME", "USERNAME", "ROLE", "STATUS");
            System.out.println(
                    "--------------------------------------------------------------------------------"
            );

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-10d %-20s %-20s %-18s %-12s%n",
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("status"));
            }

            if (!found) {
                System.out.println("No users found.");
            }
            System.out.println("======================================");

        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }
    }


    public void viewMakeupArtists() {
        String sql = "SELECT user_id, name, username, status FROM Users WHERE role = 'MAKEUP_ARTIST' ORDER BY user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n========== MAKEUP ARTISTS ==========");
            System.out.printf("%-10s %-20s %-20s %-12s%n",
                    "USER ID", "NAME", "USERNAME", "STATUS");
            System.out.println(
                    "------------------------------------------------------------"
            );

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-10d %-20s %-20s %-12s%n",
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("status"));
            }

            if (!found) {
                System.out.println("No makeup artists found.");
            }
            System.out.println("======================================");

        } catch (SQLException e) {
            System.out.println("Error retrieving makeup artists: " + e.getMessage());
        }
    }

    public void viewCustomerInfo(int customerId) {
        String sql = "SELECT user_id, name, username, status FROM Users WHERE user_id = ? AND role = 'CUSTOMER'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n========== CUSTOMER INFORMATION ==========");
                    System.out.println("Customer ID : " + rs.getInt("user_id"));
                    System.out.println("Name        : " + rs.getString("name"));
                    System.out.println("Username    : " + rs.getString("username"));
                    System.out.println("Status      : " + rs.getString("status"));
                    System.out.println("==========================================");
                } else {
                    System.out.println("Customer not found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving customer information: " + e.getMessage());
        }
    }

    public int getUserIdByUsername(String username) {
        String sql = "SELECT user_id FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting user ID: " + e.getMessage());
        }
        return -1;
    }
}