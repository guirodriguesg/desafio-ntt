package nttdata.bank.domain.dto.transacao;

import nttdata.bank.domain.entities.conta.Conta;
import nttdata.bank.domain.entities.transacao.StatusTransacaoEnum;
import nttdata.bank.domain.entities.transacao.TipoDespesaEnum;
import nttdata.bank.domain.entities.transacao.TipoTransacaoFinEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public record TransacaoDTO(Long id, Conta contaOrigem, Conta contaDestino, TipoTransacaoFinEnum tipoTransacaoFinanceira,
                           BigDecimal valorTransacao, LocalDateTime dataTransacao, BigDecimal taxaCambio,
                           TipoDespesaEnum tipoDespesa, StatusTransacaoEnum statusTransacao) {

    public String getCodigoEDigitoContaOrigem() {
        return contaOrigem.getCodConta().concat("-").concat(contaOrigem.getDigitoConta());
    }
    public String getCodigoEDigitoContaDestino() {
        return Objects.isNull(contaDestino) ? " - " : contaDestino.getCodConta().concat("-").concat(contaDestino.getDigitoConta());
    }
}
