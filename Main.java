import java.util.Scanner;

import library.Book;
import library.LibraryManager;
import library.LoanManager;
import library.Member;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite non trouvé : " + e.getMessage());
        }
        
        LibraryManager lm = new LibraryManager();
        LoanManager loanManager = new LoanManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("====== Library Management System with SQLite ======");
            System.out.println("1. List Books");
            System.out.println("2. List Reservations");
            System.out.println("3. Add Book");
            System.out.println("4. Add Member");
            System.out.println("5. Reserve Book");
            System.out.println("6. Cancel Reservation");
            System.out.println("7. Loan Book");
            System.out.println("8. Return Book");
            System.out.println("9. List Loans");
            System.out.println("10. Send Overdue Notifications");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            if (option == 0) {
                System.out.println("Exiting...");
                break;
            }

            switch (option) {
                case 1:
                    lm.listBooks();
                    break;
                case 2:
                    lm.listReservations();
                    break;
                case 3:
                    System.out.print("Enter book id: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter book author: ");
                    String author = scanner.nextLine();
                    Book newBook = new Book(id, title, author);
                    lm.addBook(newBook);
                    break;
                case 4:
                    System.out.print("Enter member id: ");
                    int mid = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter member name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter member email: ");
                    String email = scanner.nextLine();
                    Member newMember = new Member(mid, name, email);
                    lm.addMember(newMember);
                    break;
                case 5:
                    System.out.print("Enter book id to reserve: ");
                    int bid = scanner.nextInt();
                    System.out.print("Enter member id: ");
                    int mId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter reservation date (YYYY-MM-DD): ");
                    String mDate = scanner.nextLine();
                    lm.reserveBook(bid, mId, mDate);
                    break;
                case 6:
                    System.out.print("Enter book id to cancel reservation: ");
                    int cancelBookId = scanner.nextInt();
                    System.out.print("Enter member id: ");
                    int cancelMemberId = scanner.nextInt();
                    lm.cancelReservation(cancelBookId, cancelMemberId);
                    break;
                case 7:
                    System.out.print("Enter book id to loan: ");
                    int loanBookId = scanner.nextInt();
                    System.out.print("Enter member id: ");
                    int loanMemberId = scanner.nextInt();
                    loanManager.loanBook(loanBookId, loanMemberId);
                    break;
                case 8:
                    System.out.print("Enter book id to return: ");
                    int returnBookId = scanner.nextInt();
                    System.out.print("Enter member id: ");
                    int returnMemberId = scanner.nextInt();
                    loanManager.returnBook(returnBookId, returnMemberId);
                    break;
                case 9:
                    loanManager.listLoans();
                    break;
                case 10:
                    loanManager.notifyOverdueLoans();
                    break;
                default:
                    System.out.println("Invalid option");
            }
            System.out.println();
        }

        scanner.close();
    }
}
