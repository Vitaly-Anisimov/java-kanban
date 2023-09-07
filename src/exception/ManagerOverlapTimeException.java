package exception;

public class ManagerOverlapTimeException extends RuntimeException {
    public ManagerOverlapTimeException(String message) {
        super(message);
    }
}
