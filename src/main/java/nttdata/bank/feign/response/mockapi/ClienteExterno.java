package nttdata.bank.feign.response.mockapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteExterno {

    @JsonProperty("nome")
    private String nomeClienteExterno;
    @JsonProperty("banco")
    private Long codigoBanco;
    @JsonProperty("conta")
    private String codigoConta;
    @JsonProperty("saldo")
    private BigDecimal saldoClienteExterno;

}
