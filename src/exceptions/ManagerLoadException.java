package exceptions;

public class ManagerLoadException extends RuntimeException {

    public ManagerLoadException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
