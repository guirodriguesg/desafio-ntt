package nttdata.bank.handlers;

public class UsuarioException extends RuntimeException{

    private final int code;

    public UsuarioException(String message, int code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}
