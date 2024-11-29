package nttdata.bank.service.transacao;

import nttdata.bank.domain.entities.conta.Conta;
import nttdata.bank.domain.entities.transacao.StatusTransacaoEnum;
import nttdata.bank.domain.entities.transacao.TipoDespesaEnum;
import nttdata.bank.domain.entities.transacao.TipoTransacaoFinEnum;
import nttdata.bank.domain.entities.transacao.Transacao;
import nttdata.bank.repository.transacao.TransacaoRepository;
import nttdata.bank.service.adapters.ClienteExternoService;
import nttdata.bank.service.conta.ContaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransacaoService {

    private static final Logger log = LoggerFactory.getLogger(TransacaoService.class);

    private static final String MSG_CONTA_NAO_ENCONTRADA = "Conta com id {} não encontrada";

    private final TransacaoRepository transacaoRepository;
    private final ContaService contaService;
    private final ClienteExternoService clienteExternoService;

    public TransacaoService(TransacaoRepository transacaoRepository, ContaService ContaService, ClienteExternoService clienteExternoService){
        this.transacaoRepository = transacaoRepository;
        this.contaService = ContaService;
        this.clienteExternoService = clienteExternoService;
    }


    public Optional<Transacao> realizarDeposito(Transacao transacao) {
        log.info("Realizando depósito");
        Optional<Conta> conta = buscarContaPorId(transacao.getIdContaOrigem());
        if (contaNaoExiste(conta)) {
            logContaNaoEncontrada(transacao.getIdContaOrigem());
            return Optional.empty();
        }
        executaDeposito(conta.get(), transacao);
        return Optional.of(transacaoRepository.save(transacao));
    }

    private void executaDeposito(Conta conta, Transacao transacao) {
        conta.setSaldo(transacao.getValorTransacao());
        preencherTransacaoDespositoConcluida(conta, transacao);
    }

    public Optional<Transacao> realizarTransferencia(Transacao transacao) {
        log.info("Realizando transferencia da conta com id: {}", transacao.getIdContaOrigem());
        Optional<Conta> contaOrigem = buscarContaPorId(transacao.getIdContaOrigem());
        if (contaNaoExiste(contaOrigem)) {
            logContaNaoEncontrada(transacao.getContaOrigem().getId());
             return Optional.empty();
        }

        if (validarSaldoClienteExterno(transacao.getValorTransacao(),contaOrigem.get().getSaldo())) {
            log.warn("Saldo insuficiente");
            return Optional.empty();
        }

        Optional<Conta> contaDestino = buscarContaPorId(transacao.getIdContaDestino());
        if (contaNaoExiste(contaDestino)) {
            logContaNaoEncontrada(transacao.getContaDestino().getId());
            return Optional.empty();
        }

        try{
            executarTransferencia(contaOrigem.get(), contaDestino.get(), transacao);
            return Optional.of(transacaoRepository.save(transacao));
        } catch (Exception e) {
            preencherTransacaoComErro(contaOrigem.get(), transacao);
            log.error("Erro ao realizar transferencia", e);
            return Optional.of(transacaoRepository.save(transacao));
        }
    }

    private boolean validarSaldoClienteExterno(BigDecimal valorTransacao, BigDecimal saldoClienteConta) {
        return saldoClienteConta.compareTo(valorTransacao) < 0;
    }

    private void executarTransferencia(Conta origem, Conta destino, Transacao transacao) {
        origem.setSaldo(origem.getSaldo().subtract(transacao.getValorTransacao()));
        destino.setSaldo(destino.getSaldo().add(transacao.getValorTransacao()));
        preencherTransacaoTransferenciaConcluida(origem, destino, transacao);
    }

    private Optional<Conta> buscarContaPorId(Long id) {
        return contaService.buscarContaPorId(id);
    }

    private boolean contaNaoExiste(Optional<Conta> conta) {
        return conta.isEmpty();
    }

    private void logContaNaoEncontrada(Long id) {
        log.warn(MSG_CONTA_NAO_ENCONTRADA, id);
    }

    private void preencherTransacaoTransferenciaConcluida(Conta origem, Conta destino, Transacao transacao) {
        transacao.setContaOrigem(origem);
        transacao.setContaDestino(destino);
        transacao.setStatusTransacao(StatusTransacaoEnum.CONCLUIDA);
        transacao.setTipoTransacaoFinanceira(TipoTransacaoFinEnum.TRANSFERENCIA);
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setTipoDespesa(TipoDespesaEnum.OUTROS);
    }

    private void preencherTransacaoDespositoConcluida(Conta origem, Transacao transacao) {
        transacao.setContaOrigem(origem);
        transacao.setStatusTransacao(StatusTransacaoEnum.CONCLUIDA);
        transacao.setTipoTransacaoFinanceira(TipoTransacaoFinEnum.DEPOSITO);
        transacao.setDataTransacao(LocalDateTime.now());
    }

    private void preencherTransacaoComErro(Conta origem, Transacao transacao) {
        transacao.setContaOrigem(origem);
        transacao.setStatusTransacao(StatusTransacaoEnum.PENDENTE);
        transacao.setTipoTransacaoFinanceira(TipoTransacaoFinEnum.DEPOSITO);
        transacao.setDataTransacao(LocalDateTime.now());
    }

}
