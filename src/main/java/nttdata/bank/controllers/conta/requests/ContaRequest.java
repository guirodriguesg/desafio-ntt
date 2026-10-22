package nttdata.bank.controllers.conta.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ContaRequest(
        Long idConta,
        @NotNull(message = "ID do usuário é obrigatório")
        Long idUsuario,
        @NotBlank(message = "Código do banco é obrigatório")
        String codigoBanco,
        @NotBlank(message = "Código da agência é obrigatório")
        String codigoAgencia,
        @NotBlank(message = "Código da conta é obrigatório")
        String codigoConta,
        @NotBlank(message = "Digito da agência é obrigatório")
        String digitoAgencia,
        @NotBlank(message = "Digito da conta é obrigatório")
        String digitoConta,
        @NotNull(message = "Saldo é obrigatório")
        BigDecimal saldo
) {
}