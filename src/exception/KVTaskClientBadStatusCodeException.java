package exception;

public class KVTaskClientBadStatusCodeException extends RuntimeException {
    public KVTaskClientBadStatusCodeException(String message) {
        super(message);
    }
}
