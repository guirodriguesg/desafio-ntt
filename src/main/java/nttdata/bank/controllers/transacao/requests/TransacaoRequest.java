package nttdata.bank.controllers.transacao.requests;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransacaoRequest {

    @NotNull
    private Long idContaOrigem;
    @NotNull
    private Long idContaDestino;
    @NotNull
    private String tipoTransacaoFinanceira;
    @NotNull
    private BigDecimal valorTransacao;
    private BigDecimal taxaCambio;// buscar da api na hora do response
    private String tipoDespesa;

    public Long getIdContaDestino() {
        return idContaDestino;
    }

    public void setIdContaDestino(Long idContaDestino) {
        this.idContaDestino = idContaDestino;
    }

    public Long getIdContaOrigem() {
        return idContaOrigem;
    }

    public void setIdContaOrigem(Long idContaOrigem) {
        this.idContaOrigem = idContaOrigem;
    }

    public String getTipoTransacaoFinanceira() {
        return tipoTransacaoFinanceira;
    }

    public void setTipoTransacaoFinanceira(String tipoTransacaoFinanceira) {
        this.tipoTransacaoFinanceira = tipoTransacaoFinanceira;
    }

    public BigDecimal getValorTransacao() {
        return valorTransacao;
    }

    public void setValorTransacao(BigDecimal valorTransacao) {
        this.valorTransacao = valorTransacao;
    }

    public BigDecimal getTaxaCambio() {
        return taxaCambio;
    }

    public void setTaxaCambio(BigDecimal taxaCambio) {
        this.taxaCambio = taxaCambio;
    }

    public String getTipoDespesa() {
        return tipoDespesa;
    }

    public void setTipoDespesa(String tipoDespesa) {
        this.tipoDespesa = tipoDespesa;
    }
}
