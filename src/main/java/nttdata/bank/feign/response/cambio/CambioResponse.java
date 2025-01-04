package nttdata.bank.feign.response.cambio;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CambioResponse {

    @JsonProperty("success")
    private boolean statusRequisicao;
    @JsonProperty("base")
    private String moedaBase;
    @JsonProperty("date")
    private LocalDate dataTaxaCambio;
    @JsonProperty("rates")
    private Map<String, BigDecimal> cotacoes;

    private String msgResponse;
    private BigDecimal valorConvertido;

    public BigDecimal getCotacaoMoeda(String moeda){
        return this.cotacoes.isEmpty() ? BigDecimal.ZERO : this.cotacoes.get(moeda);
    }
}
