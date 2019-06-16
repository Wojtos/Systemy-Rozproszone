package requests;

import entity.Book;

public class SearchRequest extends Request {
    public SearchRequest(Book book) {
        super(book);
    }
}
