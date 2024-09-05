import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

interface Payable {
    double calculateSalary();
}

abstract class Employee implements Payable {
    private static int nextId = 1;
    protected final int id;
    protected String name;

    public Employee(String name) {
        this.id = nextId++;
        this.name = name;
    }

    public abstract double calculateSalary();

    public void displayInfo() {
        System.out.println("Employee ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Salary: $" + calculateSalary());
    }
}

class FullTimeEmployee extends Employee {
    private final double salary;

    public FullTimeEmployee(String name, double salary) {
        super(name);
        this.salary = salary;
    }

    @Override
    public double calculateSalary() {
        return salary;
    }
}

class PartTimeEmployee extends Employee {
    private final double hourlyRate;
    private double hoursWorked;

    public PartTimeEmployee(String name, double hourlyRate) {
        super(name);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = 0;
    }

    public void clockIn(double hours) {
        this.hoursWorked += hours;
    }

    @Override
    public double calculateSalary() {
        return hourlyRate * hoursWorked;
    }
}

public class EmployeePayrollSystem {
    private static List<Employee> employees = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/EmployeePayrollDB";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";

    public static void main(String[] args) {
        // Establish connection to the database
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            System.out.println("Connected to the database!");

            // Create the Employee table if it doesn't exist
            createEmployeeTable(connection);

            // Insert sample data into the Employee table if it's empty
            if (isEmployeeTableEmpty(connection)) {
                insertSampleData(connection);
            }

            // Starting the Employee Payroll System
            printWelcomeMessage();
            while (true) {
                displayMenu();
                int choice = getChoice();
                processChoice(choice, connection);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }

    private static void createEmployeeTable(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Employee ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "name VARCHAR(100),"
                + "employee_type VARCHAR(20),"
                + "salary DECIMAL(10, 2),"
                + "hourly_rate DECIMAL(10, 2),"
                + "hours_worked DECIMAL(10, 2)"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
            System.out.println("Employee table created successfully!");
        }
    }

    private static boolean isEmployeeTableEmpty(Connection connection) throws SQLException {
        String countRowsSQL = "SELECT COUNT(*) AS count FROM Employee";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(countRowsSQL)) {
            resultSet.next();
            int count = resultSet.getInt("count");
            return count == 0;
        }
    }

    private static void insertSampleData(Connection connection) throws SQLException {
        String insertFullTimeEmployeeSQL = "INSERT INTO Employee (name, employee_type, salary)"
                + "VALUES (?, ?, ?)";

        String insertPartTimeEmployeeSQL = "INSERT INTO Employee (name, employee_type, hourly_rate, hours_worked)"
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertFullTimeEmployeeSQL)) {
            statement.setString(1, "John Doe");
            statement.setString(2, "Full-time");
            statement.setDouble(3, 50000.00);
            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement(insertPartTimeEmployeeSQL)) {
            statement.setString(1, "Jane Smith");
            statement.setString(2, "Part-time");
            statement.setDouble(3, 20.00);
            statement.setDouble(4, 30.0);
            statement.executeUpdate();
        }
    }

    private static void printWelcomeMessage() {
        System.out.println("Welcome to the Maze Soft Company");
        System.out.println("--------------------------------");
    }

    private static void displayMenu() {
        System.out.println("Employee Payroll System Menu:");
        System.out.println("-----------------------------");
        System.out.println("1. Add Full-Time Employee");
        System.out.println("2. Add Part-Time Employee");
        System.out.println("3. Clock In Part-Time Employee");
        System.out.println("4. Display Employee Info");
        System.out.println("5. Display All Employee Details");
        System.out.println("6. Exit");
        System.out.println("-----------------------------");
        System.out.print("Select an option: ");
    }

    private static int getChoice() {
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // clear the input buffer
            return getChoice();
        }
    }

    private static void processChoice(int choice, Connection connection) throws SQLException {
        switch (choice) {
            case 1:
                addFullTimeEmployee(connection);
                break;
            case 2:
                addPartTimeEmployee(connection);
                break;
            case 3:
                clockInPartTimeEmployee(connection);
                break;
            case 4:
                displayEmployeeInfo(connection);
                break;
            case 5:
                displayAllEmployeeDetails(connection);
                break;
            case 6:
                exitSystem();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void addFullTimeEmployee(Connection connection) throws SQLException {
        System.out.print("Enter full-time employee name: ");
        scanner.nextLine();
        String fullName = scanner.nextLine();
        System.out.print("Enter salary: $");
        double salary = scanner.nextDouble();

        String insertFullTimeEmployeeSQL = "INSERT INTO Employee (name, employee_type, salary)"
                + "VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertFullTimeEmployeeSQL)) {
            statement.setString(1, fullName);
            statement.setString(2, "Full-time");
            statement.setDouble(3, salary);
            statement.executeUpdate();
            System.out.println("Full-time employee added successfully!");
            System.out.println("-----------------------------");
        }
    }

    private static void addPartTimeEmployee(Connection connection) throws SQLException {
        System.out.print("Enter part-time employee name: ");
        scanner.nextLine();
        String partTimeName = scanner.nextLine();
        System.out.print("Enter hourly rate: $");
        double hourlyRate = scanner.nextDouble();
        System.out.print("Enter hours worked: ");
        double hoursWorked = scanner.nextDouble();

        String insertPartTimeEmployeeSQL = "INSERT INTO Employee (name, employee_type, hourly_rate, hours_worked)"
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertPartTimeEmployeeSQL)) {
            statement.setString(1, partTimeName);
            statement.setString(2, "Part-time");
            statement.setDouble(3, hourlyRate);
            statement.setDouble(4, hoursWorked);
            statement.executeUpdate();
            System.out.println("Part-time employee added successfully!");
            System.out.println("-----------------------------");
        }
    }

    private static void clockInPartTimeEmployee(Connection connection) throws SQLException {
        System.out.print("Enter the ID of the part-time employee to clock in: ");
        int partTimeId = scanner.nextInt();
        System.out.print("Enter hours worked: ");
        double hoursWorked = scanner.nextDouble();

        String updateHoursWorkedSQL = "UPDATE Employee SET hours_worked = hours_worked + ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateHoursWorkedSQL)) {
            statement.setDouble(1, hoursWorked);
            statement.setInt(2, partTimeId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Part-time employee's hours updated successfully!");
            } else {
                System.out.println("No employee found with the given ID.");
            }
            System.out.println("-----------------------------");
        }
    }

    private static void displayEmployeeInfo(Connection connection) throws SQLException {
        System.out.print("Enter the ID of the employee to display info: ");
        int displayId = scanner.nextInt();

        String selectEmployeeSQL = "SELECT * FROM Employee WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(selectEmployeeSQL)) {
            statement.setInt(1, displayId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String type = resultSet.getString("employee_type");
                    double salary = resultSet.getDouble("salary");
                    double hourlyRate = resultSet.getDouble("hourly_rate");
                    double hoursWorked = resultSet.getDouble("hours_worked");

                    System.out.println("-----------------------------");
                    System.out.println("Employee ID: " + id);
                    System.out.println("Name: " + name);
                    System.out.println("Employee Type: " + type);
                    if (type.equals("Full-time")) {
                        System.out.println("Salary: $" + salary);
                    } else {
                        System.out.println("Hourly Rate: $" + hourlyRate);
                        System.out.println("Hours Worked: " + hoursWorked);
                    }
                    System.out.println("-----------------------------");
                } else {
                    System.out.println("No employee found with the given ID.");
                }
            }
        }
    }

    private static void displayAllEmployeeDetails(Connection connection) throws SQLException {
        String selectAllEmployeesSQL = "SELECT * FROM Employee";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectAllEmployeesSQL)) {
            System.out.println("--------------------------------");
            System.out.println("All Employee Details:");
            System.out.println("--------------------------------");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String type = resultSet.getString("employee_type");
                double salary = resultSet.getDouble("salary");
                double hourlyRate = resultSet.getDouble("hourly_rate");
                double hoursWorked = resultSet.getDouble("hours_worked");

                System.out.println("-----------------------------");
                System.out.println("Employee ID: " + id);
                System.out.println("Name: " + name);
                System.out.println("Employee Type: " + type);
                if (type.equals("Full-time")) {
                    System.out.println("Salary: $" + salary);
                } else {
                    System.out.println("Hourly Rate: $" + hourlyRate);
                    System.out.println("Hours Worked: " + hoursWorked);
                }
            }
            System.out.println("-----------------------------");
        }
    }

    private static void exitSystem() {
        System.out.println("Exiting Employee Payroll System. Goodbye!");
        System.exit(0);
    }
}
