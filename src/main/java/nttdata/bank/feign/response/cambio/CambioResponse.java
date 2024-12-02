package nttdata.bank.feign.response.cambio;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class CambioResponse {

    @JsonProperty("success")
    private boolean statusRequisicao;
    @JsonProperty("base")
    private String moedaBase;
    @JsonProperty("date")
    private LocalDate dataTaxaCambio;
    @JsonProperty("rates")
    private Map<String, BigDecimal> cotacoes;

    private BigDecimal valorConvertido;

    public boolean isStatusRequisicao() {
        return statusRequisicao;
    }

    public void setStatusRequisicao(boolean statusRequisicao) {
        this.statusRequisicao = statusRequisicao;
    }

    public LocalDate getDataTaxaCambio() {
        return dataTaxaCambio;
    }

    public void setDataTaxaCambio(LocalDate dataTaxaCambio) {
        this.dataTaxaCambio = dataTaxaCambio;
    }

    public String getMoedaBase() {
        return moedaBase;
    }

    public void setMoedaBase(String moedaBase) {
        this.moedaBase = moedaBase;
    }

    public Map<String, BigDecimal> getCotacoes() {
        return cotacoes;
    }

    public void setCotacoes(Map<String, BigDecimal> cotacoes) {
        this.cotacoes = cotacoes;
    }

    public BigDecimal getValorConvertido() {
        return valorConvertido;
    }

    public void setValorConvertido(BigDecimal valorConvertido) {
        this.valorConvertido = valorConvertido;
    }
}
