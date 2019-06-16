package responses;

public class OrderResponse extends Response {
    public OrderResponse(String message) {
        super(message);
    }

    public static class NoOrderResponse extends OrderResponse {
        public NoOrderResponse() {
            super("ORDER CANNOT BE DONE");
        }
    }
}
