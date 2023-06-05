package tasks;

public enum Status {
    NEW("NEW"),
    IN_PROGRESS("IN_PROGRESS"),
    DONE("DONE"),
    UNDEFINED ("");
    private String statusChoose;

    Status(String statusChoose) {
        this.statusChoose = statusChoose;
    }

    public static Status valueOfString(final String value) {
        for (Status status : values()) {
            if (status.statusChoose.equals(value.toUpperCase())) {
                return status;
            }
        }
        return UNDEFINED;
    }



}
