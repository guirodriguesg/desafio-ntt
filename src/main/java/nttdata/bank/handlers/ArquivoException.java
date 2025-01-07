package nttdata.bank.handlers;

public class ArquivoException extends RuntimeException {

    private final int code;

    public ArquivoException(String message, int code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}
