package library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class LibraryManager {
    public List<Book> books;
    public List<Member> members;
    public List<Reservation> reservations;
    public Connection connection;

    public LibraryManager() {
        books = new ArrayList<>();
        members = new ArrayList<>();
        reservations = new ArrayList<>();

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:/workspaces/conception-refactoring/library.db");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        loadBooks();
        loadMembers();
        loadReservations();
    }

    public void loadBooks() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM books");
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean isReserved = rs.getInt("isReserved") == 1;
                Book b = new Book(id, title, author);
                b.isReserved = isReserved;
                books.add(b);
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
    }

    public void loadMembers() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM members");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                Member m = new Member(id, name, email);
                members.add(m);
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error loading members: " + e.getMessage());
        }
    }

    public void loadReservations() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT b.id AS bId, b.title, b.author, m.id AS mId, m.name, m.email, r.reservationDate FROM reservations AS r INNER JOIN books as b ON r.bookId == b.id INNER JOIN members as m ON r.memberId == m.id");
            while (rs.next()) {
                Book b = new Book(
                    rs.getInt("bId"),
                    rs.getString("title"),
                    rs.getString("author"));
                Member m = new Member(
                    rs.getInt("mId"),
                    rs.getString("name"),
                    rs.getString("email"));
                String dateStr = rs.getString("reservationDate");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Reservation r = new Reservation(
                        b, m, sdf.parse(dateStr));
                reservations.add(r);
            }
            stmt.close();
        } catch (ParseException e) {
            System.out.println("Error loading resevations, parsing date: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error loading resevations: " + e.getMessage());
        }
    }

    public void addBook(Book book) {
        if (book == null) {
            System.out.println("Error: Book is null");
            return;
        }
        books.add(book);
        System.out.println("Added book: " + book.getInfo());
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("INSERT INTO books VALUES (" + book.id + ", '" + book.title + "', '" + book.author
                    + "', " + (book.isReserved ? 1 : 0) + ")");
            stmt.close();
        } catch (SQLException e) {
            System.out.println("DB Error adding book: " + e.getMessage());
        }
    }

    public void addMember(Member member) {
        if (member == null) {
            System.out.println("Error: Member is null");
            return;
        }
        members.add(member);
        System.out.println("Added member: " + member.name);
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(
                    "INSERT INTO members VALUES (" + member.id + ", '" + member.name + "', '" + member.email + "')");
            stmt.close();
        } catch (SQLException e) {
            System.out.println("DB Error adding member: " + e.getMessage());
        }
    }

    public void reserveBook(int bookId, int memberId, String reservationDate) {
        Book book = null;
        for (Book b : books) {
            if (b.id == bookId) {
                book = b;
                break;
            }
        }
        if (book == null) {
            System.out.println("Error: Book not found");
            return;
        }
        if (book.isReserved) {
            System.out.println("Error: Book already reserved");
            return;
        }
        Member member = null;
        for (Member m : members) {
            if (m.id == memberId) {
                member = m;
                break;
            }
        }
        if (member == null) {
            System.out.println("Error: Member not found");
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = sdf.parse(reservationDate);
            
            Reservation reservation = new Reservation(book, member, parsedDate);
            reservations.add(reservation);
            book.isReserved = true;
            System.out.println("Reservation made: " + reservation.getReservationInfo());
            
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("INSERT INTO reservations VALUES (" + book.id + ", " + member.id + ", '"
            + sdf.format(reservation.reservationDate) + "')");
            stmt.executeUpdate("UPDATE books SET isReserved = 1 WHERE id = " + book.id);
            stmt.close();
        } catch (ParseException e) {
            System.out.println("Input error, resevation date: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("DB Error reserving book: " + e.getMessage());
        }
    }

    public void cancelReservation(int bookId, int memberId) {
        Reservation found = null;
        for (Reservation r : reservations) {
            if (r.book.id == bookId && r.member.id == memberId) {
                found = r;
                break;
            }
        }
        if (found == null) {
            System.out.println("Error: Reservation not found");
            return;
        }
        found.book.isReserved = false;
        reservations.remove(found);
        System.out.println("Cancelled reservation for book: " + found.book.getInfo());
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM reservations WHERE bookId = " + bookId + " AND memberId = " + memberId);
            stmt.executeUpdate("UPDATE books SET isReserved = 0 WHERE id = " + bookId);
            stmt.close();
        } catch (SQLException e) {
            System.out.println("DB Error cancelling reservation: " + e.getMessage());
        }
    }

    public void listBooks() {
        System.out.println("List of books:");
        for (Book b : books) {
            System.out.println(b.getInfo());
        }
    }

    public void listReservations() {
        System.out.println("List of reservations:");
        for (Reservation r : reservations) {
            System.out.println(r.getReservationInfo());
        }
    }
}
