package com.scc.menu;

import com.scc.dao.AppointmentDao;
import com.scc.dao.ServiceDao;
import com.scc.dao.UserDao;
import java.util.Scanner;

public class CustomerMenu {

    private final Scanner sc;
    private final UserDao userDao;
    private final String username;

    public CustomerMenu(
            Scanner sc,
            UserDao userDao,
            String username) {

        this.sc = sc;
        this.userDao = userDao;
        this.username = username;
    }

    public void show() {

        boolean running = true;
        ServiceDao serviceDao = new ServiceDao();
        AppointmentDao appointmentDao = new AppointmentDao();

        while (running) {

            System.out.println("\n===================================");
            System.out.println("       GLAMOURA CUSTOMER PORTAL");
            System.out.println("===================================");
            System.out.println("Welcome, " + username + "!");
            System.out.println("1. Browse Makeup Services");
            System.out.println("2. Book an Appointment");
            System.out.println("3. View My Appointments");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {

                case "1":

                    serviceDao.viewServices();
                    break;

                case "2":

                    System.out.println("\n=== BOOK AN APPOINTMENT ===");
                    serviceDao.viewServices();

                    System.out.print("\nEnter Service ID to book: ");
                    int serviceId;
                    try {
                        serviceId = Integer.parseInt(sc.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Service ID format.");
                        break;
                    }

                    userDao.viewMakeupArtists();
                    System.out.print("\nEnter Makeup Artist User ID: ");
                    int artistId;
                    try {
                        artistId = Integer.parseInt(sc.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Artist ID format.");
                        break;
                    }

                    System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
                    String appointmentDate = sc.nextLine().trim();

                    int customerId = userDao.getUserIdByUsername(username);
                    if (customerId != -1) {
                        appointmentDao.bookAppointment(customerId, artistId, serviceId, appointmentDate);
                    } else {
                        System.out.println("Error: Could not retrieve customer ID.");
                    }
                    break;

                case "3":

                    int custId = userDao.getUserIdByUsername(username);
                    if (custId != -1) {
                        appointmentDao.viewCustomerAppointments(custId);

                        System.out.print("\nWould you like to cancel a pending appointment? (y/n): ");
                        String cancelOpt = sc.nextLine().trim();
                        if (cancelOpt.equalsIgnoreCase("y")) {
                            System.out.print("Enter Appointment ID to cancel: ");
                            try {
                                int apptId = Integer.parseInt(sc.nextLine().trim());
                                appointmentDao.cancelAppointment(apptId, custId);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid Appointment ID format.");
                            }
                        }
                    } else {
                        System.out.println("Error: Could not retrieve customer ID.");
                    }
                    break;

                case "4":
                    running = false;
                    System.out.println(
                            "\nThank you for choosing Glamoura!"
                    );
                    System.out.println("Logging out...");
                    break;

                default:
                    System.out.println(
                            "Invalid option. Please choose 1, 2, 3, or 4."
                    );
            }
        }
    }
}