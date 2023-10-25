import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

class Book {
    private String name;
    private String isbn;
    private String author;
    private String publicationDate;

    public Book(String name, String isbn, String author, String publicationDate) {
        this.name = name;
        this.isbn = isbn;
        this.author = author;
        this.publicationDate = publicationDate;
    }

    public String getName() {
        return name;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublicationDate() {
        return publicationDate;
    }
}


public class LibraryManagementApp {
    private static final String DATABASE_URL = "jdbc:sqlite:/Users/shaivyajhadiyal/Downloads/testdb";
    private static Connection connection;

    public static void main(String[] args) {
        try {
            // Create the database connection
            try {
                connection = DriverManager.getConnection(DATABASE_URL);
            }
            catch(Exception e) {
                System.out.print("connection failed with error: " + e.getMessage());
            }
            initializeDatabase();

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nLibrary Management System");
                System.out.println("1. Add Book");
                System.out.println("2. Update Book");
                System.out.println("3. Delete Book");
                System.out.println("4. Fetch Book Details");
                System.out.println("5. Exit");
                System.out.print("Select an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        addBook();
                        break;
                    case 2:
                        updateBook();
                        break;
                    case 3:
                        deleteBook();
                        break;
                    case 4:
                        fetchBookDetails();
                        break;
                    case 5:
                        System.out.println("Goodbye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void initializeDatabase() throws SQLException {
        // Create the 'books' table if it doesn't exist
        String createTableSQL = "CREATE TABLE IF NOT EXISTS books ("
                + "name TEXT,"
                + "isbn TEXT PRIMARY KEY,"
                + "author TEXT,"
                + "publicationDate TEXT)";
        try (PreparedStatement stmt = connection.prepareStatement(createTableSQL)) {
            stmt.execute();
        }
        catch (Exception e) {
            System.out.println("unable to create table");
        }
    }
    // to verify if a book is added to the DB
    private static boolean isBookAdded(String isbn) throws SQLException {
        String querySQL = "SELECT * FROM books WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(querySQL)) {
            stmt.setString(1, isbn);
            try (ResultSet resultSet = stmt.executeQuery()) {
                return ((ResultSet) resultSet).next(); // If a result is returned, the book exists
            }
        }
    }

    private static void addBook() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String querySql = "INSERT INTO books(name, isbn, author, publicationDate) VALUES(?,?,?,?)";
        System.out.print("Enter the book name: ");
        String name = scanner.nextLine();

        System.out.print("Enter the ISBN: ");
        String isbn = scanner.nextLine();

        System.out.print("Enter the author: ");
        String author = scanner.nextLine();

        System.out.print("Enter the publication date: ");
        String publicationDate = scanner.nextLine();

        try (PreparedStatement pstmt = connection.prepareStatement(querySql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, isbn);
            pstmt.setString(3, author);
            pstmt.setString(4, publicationDate);

            pstmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (isBookAdded(isbn)) {
            System.out.println("Book added successfully!");
        } else {
            System.out.println("Failed to add book.");
        }
    }

    public static void updateBook() throws SQLException {
        String queryUpdate = "UPDATE books SET name = ?, "
                + "author = ?, "
                + "publicationDate = ? "
                + "WHERE isbn = ?";

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the ISBN: ");
        String isbn = scanner.nextLine();

        System.out.print("Enter the book name: ");
        String newName = scanner.nextLine();

        System.out.print("Enter the author: ");
        String author = scanner.nextLine();

        System.out.print("Enter the publication date: ");
        String publicationDate = scanner.nextLine();

        try(PreparedStatement pstmt = connection.prepareStatement(queryUpdate)) {

            // set the corresponding param
            pstmt.setString(1, newName);
            pstmt.setString(2, author);
            pstmt.setString(3, publicationDate);
            pstmt.setString(4, isbn);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteBook() {
        String queryDelete = "DELETE FROM books WHERE isbn = ?";

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the ISBN: ");
        String isbn = scanner.nextLine();

        try (PreparedStatement pstmt = connection.prepareStatement(queryDelete)) {

            // set the corresponding param
            pstmt.setString(1, isbn);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void fetchBookDetails() {
        String queryFetch = "SELECT isbn, name, author, publicationDate FROM books";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(queryFetch)){

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("isbn") +  "\t" +
                        rs.getString("name") + "\t" +
                        rs.getString("author") + "\t" +
                        rs.getString("publicationDate"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
