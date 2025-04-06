package library;

import java.util.Date;

public class Reservation {
    public Book book;
    public Member member;
    public Date reservationDate;

    public Reservation(Book book, Member member, Date resDate) {
        this.book = book;
        this.member = member;
        this.reservationDate = resDate;
    }

    public String getReservationInfo() {
        return "Reservation: " + book.getInfo() + " reserved by " + member.name + " on " + reservationDate;
    }
}
