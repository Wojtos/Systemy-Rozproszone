package responses;


public abstract class TransactionalResponse extends Response {
    private int transactionId;

    public TransactionalResponse(String message, int transactionId) {
        super(message);
        this.transactionId = transactionId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
}
