package responses;

public class PriceResponse extends TransactionalResponse {
    public PriceResponse(String message, int transactionId) {
        super(message, transactionId);
    }

    public static class NoPriceResponse extends PriceResponse {
        public NoPriceResponse(int transactionId) {
            super("", transactionId);
        }
    }
}
