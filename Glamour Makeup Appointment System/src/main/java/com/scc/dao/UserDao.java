package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Locale;
import java.sql.SQLException;

public class UserDao {

    public boolean registerUser(String username, String password, String role){

        String sql = "INSERT INTO Users (username, password, role, status) " +
                "VALUES (?, ?, ?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toUpperCase(Locale.ROOT));
            pstmt.executeUpdate();
            System.out.println("-> Successful Registration, please wait for your approval.");
            return true;

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }

    }
}
