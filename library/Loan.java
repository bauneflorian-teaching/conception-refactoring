package library;

import java.util.Date;

public class Loan {
    public Book book;
    public Member member;
    public Date loanDate;
    public Date dueDate;
    public boolean returned;

    public Loan(Book book, Member member, Date loanDate, Date dueDate) {
        this.book = book;
        this.member = member;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returned = false;
    }

    public String getLoanInfo() {
        return "Loan: " + book.getInfo() + " borrowed by " + member.name + " due on " + dueDate;
    }
}
