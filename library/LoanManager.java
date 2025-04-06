package library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

public class LoanManager {
    public List<Loan> loans;
    public Connection connection;

    public LoanManager() {
        loans = new ArrayList<>();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:library.db");
        } catch (SQLException e) {
            System.out.println("DB Error in LoanManager constructor: " + e.getMessage());
        }

        loadLoans();
    }

    public void loadLoans() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT b.id AS bId, b.title, b.author, m.id AS mId, m.name, m.email, l.loanDate, l.dueDate FROM loans AS l INNER JOIN books as b ON l.bookId == b.id INNER JOIN members as m ON l.memberId == m.id WHERE l.returned == 0");
            while (rs.next()) {
                Book b = new Book(
                    rs.getInt("bId"),
                    rs.getString("title"),
                    rs.getString("author"));
                Member m = new Member(
                    rs.getInt("mId"),
                    rs.getString("name"),
                    rs.getString("email"));
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date loanDate = sdf.parse(rs.getString("loanDate"));
                Date dueDate = sdf.parse(rs.getString("dueDate"));
                Loan l = new Loan(
                        b, m, loanDate, dueDate);
                loans.add(l);
            }
            stmt.close();
        } catch (ParseException e) {
            System.out.println("Error loading loans, parsing date: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error loading loans: " + e.getMessage());
        }
    }

    public void loanBook(int bookId, int memberId) {
        LibraryManager lm = new LibraryManager();
        Book book = null;
        Member member = null;
        for (Book b : lm.books) {
            if (b.id == bookId) {
                book = b;
                break;
            }
        }
        for (Member m : lm.members) {
            if (m.id == memberId) {
                member = m;
                break;
            }
        }
        if (book == null || member == null) {
            System.out.println("Error: Book or Member not found for loan");
            return;
        }
        if (book.isReserved) {
            System.out.println("Error: Book is reserved and cannot be loaned");
            return;
        }
        book.isReserved = true;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        Date loanDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 14);
        Date dueDate = cal.getTime();
        Loan loan = new Loan(book, member, loanDate, dueDate);

        loans.add(loan);
        System.out.println("Book loaned: " + loan.getLoanInfo());
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("INSERT INTO loans VALUES (" + book.id + ", " + member.id + ", '" + sdf.format(loan.loanDate)
                    + "', '" + sdf.format(loan.dueDate) + "', 0)");
            stmt.close();
        } catch (SQLException e) {
            System.out.println("DB Error loaning book: " + e.getMessage());
        }
    }

    public void returnBook(int bookId, int memberId) {
        Loan found = null;
        for (Loan l : loans) {
            if (l.book.id == bookId && l.member.id == memberId && !l.returned) {
                found = l;
                break;
            }
        }
        if (found == null) {
            System.out.println("Error: Loan record not found");
            return;
        }
        found.returned = true;
        found.book.isReserved = false;
        long currentTime = new Date().getTime();
        if (currentTime > found.dueDate.getTime()) {
            long daysOverdue = (currentTime - found.dueDate.getTime()) / (24 * 60 * 60 * 1000);
            double fine = daysOverdue * 1.5;
            System.out.println("Book returned with a fine of $" + fine);
        } else {
            System.out.println("Book returned on time");
        }

        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("UPDATE loans SET returned = 1 WHERE bookId = " + bookId + " AND memberId = " + memberId
                    + " AND returned = 0");
            stmt.close();
        } catch (SQLException e) {
            System.out.println("DB Error returning book: " + e.getMessage());
        }
    }

    public void listLoans() {
        System.out.println("List of loans:");
        for (Loan l : loans) {
            if (!l.returned) {
                System.out.println(l.getLoanInfo());
            }
        }
    }

    public void notifyOverdueLoans() {
        NotificationService ns = new NotificationService();
        Date now = new Date();
        for (Loan l : loans) {
            if (!l.returned && now.after(l.dueDate)) {
                ns.sendNotification(l.member, "Your loan for book '" + l.book.title + "' is overdue!");
            }
        }
    }
}
