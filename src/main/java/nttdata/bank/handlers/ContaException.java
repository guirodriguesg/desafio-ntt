package nttdata.bank.handlers;

public class ContaException extends RuntimeException {

    private final int code;

    public ContaException(String message, int code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}
