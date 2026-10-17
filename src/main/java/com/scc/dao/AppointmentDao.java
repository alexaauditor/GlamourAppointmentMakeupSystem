package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AppointmentDao {

    public void viewAllAppointments() {
        String sql = "SELECT a.appointment_id, c.name AS customer_name, ar.name AS artist_name, " +
                "s.service_name, a.appointment_date, a.status " +
                "FROM Appointments a " +
                "JOIN Users c ON a.customer_id = c.user_id " +
                "JOIN Users ar ON a.artist_id = ar.user_id " +
                "JOIN Services s ON a.service_id = s.service_id " +
                "ORDER BY a.appointment_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n========== ALL SYSTEM APPOINTMENTS ==========");
            System.out.printf("%-6s %-18s %-18s %-20s %-15s %-12s%n",
                    "ID", "CUSTOMER", "ARTIST", "SERVICE", "DATE", "STATUS");
            System.out.println(
                    "------------------------------------------------------------------------------------------------"
            );

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-6d %-18s %-18s %-20s %-15s %-12s%n",
                        rs.getInt("appointment_id"),
                        rs.getString("customer_name"),
                        rs.getString("artist_name"),
                        rs.getString("service_name"),
                        rs.getString("appointment_date"),
                        rs.getString("status")
                );
            }

            if (!found) {
                System.out.println("No appointments found in the system.");
            }
            System.out.println("==============================================");

        } catch (SQLException e) {
            System.out.println("Error fetching all appointments: " + e.getMessage());
        }
    }

    public void viewAssignedAppointments(int artistId) {
        String sql = "SELECT a.appointment_id, c.name AS customer_name, a.appointment_date, a.status " +
                "FROM Appointments a JOIN Users c ON a.customer_id = c.user_id " +
                "WHERE a.artist_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, artistId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n========== YOUR ASSIGNED APPOINTMENTS ==========");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("ID: %d | Customer: %s | Date: %s | Status: %s%n",
                        rs.getInt("appointment_id"),
                        rs.getString("customer_name"),
                        rs.getString("appointment_date"),
                        rs.getString("status")
                );
            }
            if (!found) {
                System.out.println("No assigned appointments found.");
            }
            System.out.println("================================================");
        } catch (SQLException e) {
            System.out.println("Error fetching appointments: " + e.getMessage());
        }
    }

    public boolean updateAppointmentStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE Appointments SET status = ? WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus.toUpperCase());
            pstmt.setInt(2, appointmentId);
            pstmt.executeUpdate();
            System.out.println("Appointment status updated successfully.");
            return true;
        } catch (SQLException e) {
            System.out.println("Error updating appointment: " + e.getMessage());
            return false;
        }
    }

    public boolean bookAppointment(int customerId, int artistId, int serviceId, String appointmentDate) {
        String sql = "INSERT INTO Appointments (customer_id, artist_id, service_id, appointment_date, status) VALUES (?, ?, ?, ?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            pstmt.setInt(2, artistId);
            pstmt.setInt(3, serviceId);
            pstmt.setString(4, appointmentDate);
            pstmt.executeUpdate();

            System.out.println("Appointment successfully requested! Status: PENDING");
            return true;

        } catch (SQLException e) {
            System.out.println("Booking failed: " + e.getMessage());
            return false;
        }
    }

    public void viewCustomerAppointments(int customerId) {
        String sql = "SELECT a.appointment_id, s.service_name, ar.name AS artist_name, a.appointment_date, a.status " +
                "FROM Appointments a " +
                "JOIN Services s ON a.service_id = s.service_id " +
                "JOIN Users ar ON a.artist_id = ar.user_id " +
                "WHERE a.customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n========== MY APPOINTMENT HISTORY ==========");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("ID: %d | Service: %s | Artist: %s | Date: %s | Status: %s%n",
                        rs.getInt("appointment_id"),
                        rs.getString("service_name"),
                        rs.getString("artist_name"),
                        rs.getString("appointment_date"),
                        rs.getString("status")
                );
            }
            if (!found) {
                System.out.println("You have no booking history.");
            }
            System.out.println("============================================");

        } catch (SQLException e) {
            System.out.println("Error fetching your appointments: " + e.getMessage());
        }
    }

    public boolean cancelAppointment(int appointmentId, int customerId) {
        String sql = "DELETE FROM Appointments WHERE appointment_id = ? AND customer_id = ? AND status = 'PENDING'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, appointmentId);
            pstmt.setInt(2, customerId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Pending appointment successfully cancelled.");
                return true;
            }

            System.out.println("Cancellation failed. Only PENDING appointments belonging to you can be cancelled.");
            return false;

        } catch (SQLException e) {
            System.out.println("Error cancelling appointment: " + e.getMessage());
            return false;
        }
    }

    public void generateSummaryReport() {
        String countSql = "SELECT COUNT(*) AS total, " +
                "SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count, " +
                "SUM(CASE WHEN status = 'ACCEPTED' OR status = 'APPROVED' THEN 1 ELSE 0 END) AS approved_count, " +
                "SUM(CASE WHEN status = 'REJECTED' THEN 1 ELSE 0 END) AS rejected_count " +
                "FROM Appointments";

        String revenueSql = "SELECT SUM(s.price) AS total_revenue " +
                "FROM Appointments a JOIN Services s ON a.service_id = s.service_id " +
                "WHERE a.status = 'ACCEPTED' OR a.status = 'APPROVED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt1 = conn.prepareStatement(countSql);
             ResultSet rs1 = pstmt1.executeQuery();
             PreparedStatement pstmt2 = conn.prepareStatement(revenueSql);
             ResultSet rs2 = pstmt2.executeQuery()) {

            System.out.println("\n========== SYSTEM SUMMARY REPORT ==========");
            if (rs1.next()) {
                System.out.println("Total Appointments Booked : " + rs1.getInt("total"));
                System.out.println("Pending Requests          : " + rs1.getInt("pending_count"));
                System.out.println("Approved / Accepted       : " + rs1.getInt("approved_count"));
                System.out.println("Rejected Requests         : " + rs1.getInt("rejected_count"));
            }

            double totalRevenue = 0.0;
            if (rs2.next()) {
                totalRevenue = rs2.getDouble("total_revenue");
            }
            System.out.printf("Estimated Revenue         : $%.2f%n", totalRevenue);
            System.out.println("===========================================");

        } catch (SQLException e) {
            System.out.println("Error generating summary report: " + e.getMessage());
        }
    }
}