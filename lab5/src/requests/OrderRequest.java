package requests;

import entity.Book;

public class OrderRequest extends Request {
    public OrderRequest(Book book) {
        super(book);
    }
}
