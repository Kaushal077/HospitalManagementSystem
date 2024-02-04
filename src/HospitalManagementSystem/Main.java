package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Main{
    private static final String URL = "jdbc:mysql://localhost:3306/hospitalmanagementsystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "kaushal@123";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true) {
                displayMenu();
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        patient.addPatient();
                        break;
                    case 2:
                        patient.viewPatients();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        break;
                    case 4:
                    	bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;
                    case 5:
                        deleteDetails(patient, doctor, connection, scanner);
                        break;
                    case 6:
                        System.out.println("THANK YOU! FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
                        return;
                    default:
                        System.out.println("Enter valid choice!!!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayMenu() {
        System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
        System.out.println("1. Add Patient");
        System.out.println("2. View Patients");
        System.out.println("3. View Doctors");
        System.out.println("4. Book Appointment");
        System.out.println("5. Delete Details");
        System.out.println("6. Exit");
        System.out.println("Enter your choice: ");
    }

    public static void deleteDetails(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.println("Select type of details to delete:");
        System.out.println("1. Delete Patient");
        System.out.println("2. Delete Doctor");
        int deleteChoice = scanner.nextInt();

        switch (deleteChoice) {
            case 1:
                System.out.print("Enter Patient Id to delete: ");
                int patientId = scanner.nextInt();
                if (patient.deletePatient(patientId)) {
                    System.out.println("Patient details deleted successfully!");
                } else {
                    System.out.println("Failed to delete patient details. Patient not found!");
                }
                break;
            case 2:
                System.out.print("Enter Doctor Id to delete: ");
                int doctorId = scanner.nextInt();
                if (doctor.deleteDoctor(doctorId)) {
                    System.out.println("Doctor details deleted successfully!");
                } else {
                    System.out.println("Failed to delete doctor details. Doctor not found!");
                }
                break;
            default:
                System.out.println("Invalid choice for deleting details!");
        }
        
        
    }
    
    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        
        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(" +
                                          patientId + ", " + doctorId + ", '" + appointmentDate + "')";
                try {
                    Statement statement = connection.createStatement();
                    int rowsAffected = statement.executeUpdate(appointmentQuery);

                    if (rowsAffected > 0) {
                        System.out.println("Appointment Booked!");
                    } else {
                        System.out.println("Failed to Book Appointment!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor not available on this date!!");
            }
        } else {
            System.out.println("Either doctor or patient doesn't exist!!!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = " + doctorId +
                       " AND appointment_date = '" + appointmentDate + "'";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

