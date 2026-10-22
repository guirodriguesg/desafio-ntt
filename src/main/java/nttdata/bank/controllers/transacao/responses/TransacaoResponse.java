package nttdata.bank.controllers.transacao.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import nttdata.bank.domain.entities.transacao.StatusTransacaoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoResponse(
        @JsonProperty("id_transacao") Long id,
        BigDecimal taxaCambio,
        LocalDateTime dataTransacao,
        StatusTransacaoEnum statusTransacao
) {
}
