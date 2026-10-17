package com.scc.menu;

import com.scc.dao.AppointmentDao;
import com.scc.dao.ServiceDao;
import com.scc.dao.UserDao;
import java.util.Scanner;

public class MakeupArtistMenu {

    private final Scanner sc;
    private final UserDao userDao;
    private final String username;

    public MakeupArtistMenu(Scanner sc, UserDao userDao, String username) {
        this.sc = sc;
        this.userDao = userDao;
        this.username = username;
    }

    public void show() {
        boolean running = true;
        AppointmentDao appointmentDao = new AppointmentDao();
        ServiceDao serviceDao = new ServiceDao();

        while (running) {
            System.out.println("\n================================");
            System.out.println("   GLAMOURA MAKEUP ARTIST");
            System.out.println("================================");
            System.out.println("Welcome, " + username + "!");
            System.out.println("1. View My Appointments");
            System.out.println("2. View Services");
            System.out.println("3. Manage My Availability");
            System.out.println("4. View My Profile");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    int artistId = userDao.getUserIdByUsername(username);
                    if (artistId != -1) {
                        appointmentDao.viewAssignedAppointments(artistId);

                        System.out.print("\nUpdate appointment status? (y/n): ");
                        String updateOpt = sc.nextLine().trim();
                        if (updateOpt.equalsIgnoreCase("y")) {
                            System.out.print("Enter Appointment ID: ");
                            int apptId = Integer.parseInt(sc.nextLine().trim());
                            System.out.print("Enter new status (ACCEPTED/REJECTED): ");
                            String status = sc.nextLine().trim();
                            appointmentDao.updateAppointmentStatus(apptId, status);
                        }

                        System.out.print("\nView customer information? (y/n): ");
                        String custOpt = sc.nextLine().trim();
                        if (custOpt.equalsIgnoreCase("y")) {
                            System.out.print("Enter Customer User ID: ");
                            int custId = Integer.parseInt(sc.nextLine().trim());
                            userDao.viewCustomerInfo(custId);
                        }
                    }
                    break;

                case "2":
                    serviceDao.viewServices();
                    break;

                case "3":
                    System.out.println(
                            "\nAvailability management will be connected next."
                    );
                    break;

                case "4":
                    System.out.println("\n=== MY PROFILE ===");
                    System.out.println("Username: " + username);
                    System.out.println("Role: MAKEUP_ARTIST");
                    break;

                case "5":
                    System.out.println("Logging out...");
                    running = false;
                    break;

                default:
                    System.out.println(
                            "Invalid option. Please choose 1-5."
                    );
            }
        }
    }
}