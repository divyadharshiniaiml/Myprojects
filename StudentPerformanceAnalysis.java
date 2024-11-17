import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Base class: Person
class Person {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

// Derived class: Student
class Student extends Person {
    private int problemsAttempted;
    private int problemsSolved;

    public Student(String name, int problemsAttempted, int problemsSolved) {
        super(name);
        this.problemsAttempted = problemsAttempted;
        this.problemsSolved = problemsSolved;
    }

    public int getProblemsAttempted() {
        return problemsAttempted;
    }

    public int getProblemsSolved() {
        return problemsSolved;
    }

    public double getSolvePercentage() {
        return (problemsAttempted > 0) ? ((double) problemsSolved / problemsAttempted) * 100 : 0;
    }

    public String getPerformanceLevel() {
        double percentage = getSolvePercentage();
        if (percentage >= 80) {
            return "Excellent";
        } else if (percentage >= 50) {
            return "Good";
        } else {
            return "Needs Improvement";
        }
    }
}

// Generic class to manage a list of students
class StudentManager<T extends Student> {
    private List<T> students;

    public StudentManager() {
        students = new ArrayList<>();
    }

    public void addStudent(T student) {
        students.add(student);
    }

    public void saveToDatabase() {
        // Database connection details
        String url = "jdbc:mysql://localhost:3306/StudentDB";
        String username = "Divya"; // Replace with your MySQL username
        String password = "1981"; // Replace with your MySQL password

        String insertQuery = "INSERT INTO Students (name, problemsAttempted, problemsSolved, solvePercentage, performanceLevel) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            for (T student : students) {
                preparedStatement.setString(1, student.getName());
                preparedStatement.setInt(2, student.getProblemsAttempted());
                preparedStatement.setInt(3, student.getProblemsSolved());
                preparedStatement.setDouble(4, student.getSolvePercentage());
                preparedStatement.setString(5, student.getPerformanceLevel());

                preparedStatement.executeUpdate();
            }
            System.out.println("Data successfully saved to the database.");

        } catch (SQLException e) {
            System.out.println("Error saving to the database: " + e.getMessage());
        }
    }

    public void displayAllStudents() {
        for (T student : students) {
            System.out.println("\n--- Student Performance ---");
            System.out.println("Name: " + student.getName());
            System.out.println("Problems Attempted: " + student.getProblemsAttempted());
            System.out.println("Problems Solved: " + student.getProblemsSolved());
            System.out.printf("Solve Percentage: %.2f%%\n", student.getSolvePercentage());
            System.out.println("Performance Level: " + student.getPerformanceLevel());
        }
    }
}

// Main class
public class StudentPerformanceAnalysis {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentManager<Student> studentManager = new StudentManager<>();

        System.out.print("Enter the number of students: ");
        int numStudents = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        for (int i = 0; i < numStudents; i++) {
            System.out.println("\nEnter details for Student " + (i + 1));

            System.out.print("Enter name: ");
            String name = scanner.nextLine();

            System.out.print("Enter the number of problems attempted: ");
            int problemsAttempted = scanner.nextInt();

            System.out.print("Enter the number of problems solved: ");
            int problemsSolved = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            // Validate the input
            if (problemsSolved > problemsAttempted) {
                System.out.println("Error: Problems solved cannot exceed problems attempted. Please re-enter this student's data.");
                i--;
                continue;
            }

            // Add the student to the manager
            Student student = new Student(name, problemsAttempted, problemsSolved);
            studentManager.addStudent(student);
        }

        // Display all students' performance
        System.out.println("\n--- All Students' Performance ---");
        studentManager.displayAllStudents();

        // Save data to the database
        studentManager.saveToDatabase();

        scanner.close();
    }
}
