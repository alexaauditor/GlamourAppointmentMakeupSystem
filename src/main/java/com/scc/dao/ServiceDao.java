package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceDao {

    public boolean addService(String serviceName, double price, String description) {
        String sql = "INSERT INTO Services (service_name, price, description) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, serviceName);
            pstmt.setDouble(2, price);
            pstmt.setString(3, description);
            pstmt.executeUpdate();
            System.out.println("Service added successfully!");
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding service: " + e.getMessage());
            return false;
        }
    }

    public void viewServices() {
        String sql = "SELECT service_id, service_name, price, description FROM Services ORDER BY service_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n========== AVAILABLE MAKEUP SERVICES ==========");
            System.out.printf("%-10s %-25s %-10s %-30s%n",
                    "ID", "SERVICE NAME", "PRICE", "DESCRIPTION");
            System.out.println(
                    "--------------------------------------------------------------------------------"
            );

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-10d %-25s $%-9.2f %-30s%n",
                        rs.getInt("service_id"),
                        rs.getString("service_name"),
                        rs.getDouble("price"),
                        rs.getString("description")
                );
            }

            if (!found) {
                System.out.println("No services available at the moment.");
            }
            System.out.println("================================================");

        } catch (SQLException e) {
            System.out.println("Error retrieving services: " + e.getMessage());
        }
    }

    public boolean updateService(int serviceId, String serviceName, double price, String description) {
        String sql = "UPDATE Services SET service_name = ?, price = ?, description = ? WHERE service_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, serviceName);
            pstmt.setDouble(2, price);
            pstmt.setString(3, description);
            pstmt.setInt(4, serviceId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Service updated successfully!");
                return true;
            }
            System.out.println("Service ID not found.");
            return false;

        } catch (SQLException e) {
            System.out.println("Error updating service: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteService(int serviceId) {
        String sql = "DELETE FROM Services WHERE service_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, serviceId);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Service deleted successfully!");
                return true;
            }
            System.out.println("Service ID not found.");
            return false;

        } catch (SQLException e) {
            System.out.println("Error deleting service: " + e.getMessage());
            return false;
        }
    }
}