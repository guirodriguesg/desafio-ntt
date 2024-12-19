package nttdata.bank.domain.entities.transacao;

public enum StatusTransacaoEnum {

    PENDENTE("Pendente"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private String descricao;

    StatusTransacaoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
