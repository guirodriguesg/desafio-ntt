package nttdata.bank.domain.entities.transacao;

public enum TipoDespesaEnum {

    ALIMENTACAO("Alimentacao"),
    TRANSPORTE("Transporte"),
    MORADIA("Moradia"),
    SAUDE("Saude"),
    EDUCACAO("Educacao"),
    LAZER("Lazer"),
    OUTROS("Outros");

    private final String descricao;

    TipoDespesaEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
