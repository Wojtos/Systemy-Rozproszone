package responses;

public class SearchResponse extends TransactionalResponse {
    public SearchResponse(String message, int transactionId) {
        super(message, transactionId);
    }

    public static class NoSearchResponse extends SearchResponse {
        public NoSearchResponse(int transactionId) {
            super("NOT FOUND", transactionId);
        }
    }
}
