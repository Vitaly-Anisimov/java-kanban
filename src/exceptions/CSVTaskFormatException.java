package exceptions;

public class CSVTaskFormatException extends RuntimeException {
    public CSVTaskFormatException(String message) {
        super(message);
    }
}
