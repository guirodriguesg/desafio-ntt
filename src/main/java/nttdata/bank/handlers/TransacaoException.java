package nttdata.bank.handlers;

public class TransacaoException extends RuntimeException {

    private final Integer code;

    public TransacaoException(String message, int code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}
