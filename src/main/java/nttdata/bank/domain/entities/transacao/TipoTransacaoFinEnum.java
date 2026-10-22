package nttdata.bank.domain.entities.transacao;

public enum TipoTransacaoFinEnum {

    DEPOSITO("DEPOSITO"),
    RETIRADA("RETIRADA"),
    TRANSFERENCIA("TRANSFERENCIA"),
    COMPRA("COMPRA");

    private final String descricao;

    TipoTransacaoFinEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
