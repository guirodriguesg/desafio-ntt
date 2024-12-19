package nttdata.bank.controllers.transacao.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransacaoRequest(
        @NotNull(message = "ID da conta origem é obrigatório")
        Long idContaOrigem,
        @NotNull(message = "ID da conta destino é obrigatório")
        Long idContaDestino,
        @NotNull(message = "Tipo transação financeira é obrigatório")
        String tipoTransacaoFinanceira,
        @NotNull(message = "Valor é obrigatório")
        BigDecimal valorTransacao,
        BigDecimal taxaCambio,
        @NotBlank(message = "Tipo de despesa é obrigatório")
        String tipoDespesa
) {
}
