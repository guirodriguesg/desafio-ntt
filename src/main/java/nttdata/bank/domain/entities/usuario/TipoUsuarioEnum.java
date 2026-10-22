package nttdata.bank.domain.entities.usuario;

public enum TipoUsuarioEnum {

    ADMINISTRADOR("ADMINISTRADOR"),
    BANCARIO("BANCARIO"),
    CLIENTE("CLIENTE");

    private String tipo;

    TipoUsuarioEnum(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
