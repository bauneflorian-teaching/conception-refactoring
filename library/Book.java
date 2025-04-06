package library;

public class Book {
    public int id;
    public String title;
    public String author;
    public boolean isReserved;

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isReserved = false;
    }

    public String getInfo() {
        return id + ": " + title + " by " + author + (isReserved ? " (Reserved)" : "");
    }
}
