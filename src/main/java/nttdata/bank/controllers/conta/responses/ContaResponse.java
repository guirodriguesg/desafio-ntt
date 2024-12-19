package nttdata.bank.controllers.conta.responses;

import java.math.BigDecimal;

public record ContaResponse(
        Long idConta,
        Long idUsuario,
        String codigoBanco,
        String codigoAgencia,
        String codigoConta,
        String digitoAgencia,
        String digitoConta,
        BigDecimal saldo
) {
}
