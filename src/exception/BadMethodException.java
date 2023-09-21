package exception;

public class BadMethodException extends RuntimeException {
    public BadMethodException(String message) {
        super(message);
    }
}
