package exception;

public class KVTaskInterruptedOrIOException extends RuntimeException {
    public KVTaskInterruptedOrIOException(String message) {
        super(message);
    }
}
