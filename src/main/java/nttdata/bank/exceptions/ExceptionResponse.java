package nttdata.bank.exceptions;

import java.time.ZonedDateTime;

public class ExceptionResponse {

    private ZonedDateTime timestamp = ZonedDateTime.now();
    private int status;
    private String message;
    private String path;


    public ExceptionResponse(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
    }

    public static ExceptionResponse of(int status, String message,  String path) {
        return new ExceptionResponse(status, message, path);
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
