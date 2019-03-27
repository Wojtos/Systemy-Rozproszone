import java.io.Serializable;

public class Operation implements Serializable {
    enum Type {PUT, REMOVE}

    private String key;
    private Integer value;
    private Type type;

    public static Operation createOperationPut(String key, Integer value) {
        Operation operation = new Operation();
        operation.type = Type.PUT;
        operation.key = key;
        operation.value = value;
        return operation;
    }

    public static Operation createOperationRemove(String key) {
        Operation operation = new Operation();
        operation.type = Type.REMOVE;
        operation.key = key;
        return operation;
    }

    public String getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    public boolean isPut() {
        return this.type == Type.PUT;
    }
    public boolean isRemove() {
        return this.type == Type.REMOVE;
    }
}
