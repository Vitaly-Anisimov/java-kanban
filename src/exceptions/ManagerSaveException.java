package exceptions;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
