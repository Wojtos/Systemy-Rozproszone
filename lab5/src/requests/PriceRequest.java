package requests;

import entity.Book;

public class PriceRequest extends Request{
    private int transactionId;

    public PriceRequest(Book book, int transactionId) {
        super(book);
        this.transactionId = transactionId;
    }

    public int getTransactionId() {
        return transactionId;
    }

}
