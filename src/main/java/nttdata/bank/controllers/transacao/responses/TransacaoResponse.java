package nttdata.bank.controllers.transacao.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import nttdata.bank.domain.entities.transacao.StatusTransacaoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransacaoResponse {

    @JsonProperty("id_transacao")
    private Long id;

    private BigDecimal taxaCambio;
    private LocalDateTime dataTransacao;
    private StatusTransacaoEnum statusTransacao;

    public StatusTransacaoEnum getStatusTransacao() {
        return statusTransacao;
    }

    public void setStatusTransacao(StatusTransacaoEnum statusTransacao) {
        this.statusTransacao = statusTransacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTaxaCambio() {
        return taxaCambio;
    }

    public void setTaxaCambio(BigDecimal taxaCambio) {
        this.taxaCambio = taxaCambio;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }
}
