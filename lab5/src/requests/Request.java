package requests;

import entity.Book;

import java.io.Serializable;

public abstract class Request implements Serializable {
    private Book book;

    public Request(Book book) {
        this.book = book;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
