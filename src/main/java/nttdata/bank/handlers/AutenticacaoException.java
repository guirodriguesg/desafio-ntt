package nttdata.bank.handlers;

public class AutenticacaoException extends RuntimeException {

    private final Integer code;

    public AutenticacaoException(String message, int code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}
