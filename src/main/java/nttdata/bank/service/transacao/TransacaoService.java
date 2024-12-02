package nttdata.bank.service.transacao;

import aj.org.objectweb.asm.commons.Remapper;
import nttdata.bank.domain.entities.conta.Conta;
import nttdata.bank.domain.entities.transacao.StatusTransacaoEnum;
import nttdata.bank.domain.entities.transacao.TipoDespesaEnum;
import nttdata.bank.domain.entities.transacao.TipoTransacaoFinEnum;
import nttdata.bank.domain.entities.transacao.Transacao;
import nttdata.bank.repository.transacao.TransacaoRepository;
import nttdata.bank.service.adapters.ClienteExternoService;
import nttdata.bank.service.adapters.ConverteCambioService;
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
    private final ConverteCambioService converteCambioService;
    private final ClienteExternoService clienteExternoService;

    public TransacaoService(TransacaoRepository transacaoRepository, ContaService ContaService,
                            ClienteExternoService clienteExternoService, ConverteCambioService converteCambioService) {
        this.transacaoRepository = transacaoRepository;
        this.contaService = ContaService;
        this.clienteExternoService = clienteExternoService;
        this.converteCambioService = converteCambioService;
    }


    public Optional<Transacao> realizarDeposito(Transacao transacao) {
        log.info("Realizando depósito");
        Optional<Conta> conta = buscarContaPorId(transacao.getIdContaOrigem());
        if (contaNaoExiste(conta)) {
            logContaNaoEncontrada(transacao.getIdContaOrigem());
            return Optional.empty();
        }
        executarDeposito(conta.get(), transacao);
        return Optional.of(transacaoRepository.save(transacao));
    }

    private void executarDeposito(Conta conta, Transacao transacao) {
        conta.setSaldo(conta.getSaldo().add(transacao.getValorTransacao()));
        preencherTransacaoDespositoConcluida(conta, transacao);
    }

    public Optional<Transacao> realizarSaque(Transacao transacao) {
        log.info("Realizando saque");
        Optional<Conta> conta = buscarContaPorId(transacao.getIdContaOrigem());
        if (contaNaoExiste(conta)) {
            logContaNaoEncontrada(transacao.getIdContaOrigem());
            return Optional.empty();
        }

        if (validarSaldoCliente(transacao.getValorTransacao(), conta.get().getSaldo())) {
            log.warn("Saldo insuficiente");
            return Optional.empty();
        }

        executarSaque(conta.get(), transacao);
        return Optional.of(transacaoRepository.save(transacao));
    }

    private void executarSaque(Conta conta, Transacao transacao) {
        conta.setSaldo(conta.getSaldo().subtract(transacao.getValorTransacao()));
        preencherTransacaoSaqueConcluido(conta, transacao);
    }

    public Optional<Transacao> realizarTransferencia(Transacao transacao) {
        log.info("Realizando transferencia da conta com id: {}", transacao.getIdContaOrigem());
        Optional<Conta> contaOrigem = buscarContaPorId(transacao.getIdContaOrigem());
        if (contaNaoExiste(contaOrigem)) {
            logContaNaoEncontrada(transacao.getContaOrigem().getId());
            return Optional.empty();
        }

        if (validarSaldoCliente(transacao.getValorTransacao(), contaOrigem.get().getSaldo())) {
            log.warn("Saldo insuficiente");
            return Optional.empty();
        }

        Optional<Conta> contaDestino = buscarContaPorId(transacao.getIdContaDestino());
        if (contaNaoExiste(contaDestino)) {
            logContaNaoEncontrada(transacao.getContaDestino().getId());
            return Optional.empty();
        }

        try {
            executarTransferencia(contaOrigem.get(), contaDestino.get(), transacao);
            return Optional.of(transacaoRepository.save(transacao));
        } catch (Exception e) {
            preencherTransacaoComErro(contaOrigem.get(), transacao);
            log.error("Erro ao realizar transferencia", e);
            return Optional.of(transacaoRepository.save(transacao));
        }
    }

    public Optional<Transacao> realizarPagamento(Transacao transacao) {
        log.info("Realizando pagamento");
        Optional<Conta> contaOrigem = buscarContaPorId(transacao.getIdContaOrigem());
        if (contaNaoExiste(contaOrigem)) {
            logContaNaoEncontrada(transacao.getContaOrigem().getId());
            return Optional.empty();
        }
        if (validarSaldoCliente(transacao.getValorTransacao(), contaOrigem.get().getSaldo())) {
            log.warn("Saldo insuficiente");
            return Optional.empty();
        }

        executarPagamento(contaOrigem.get(), transacao);
        return Optional.of(transacaoRepository.save(transacao));
    }

    private void executarPagamento(Conta conta, Transacao transacao) {
        conta.setSaldo(conta.getSaldo().subtract(transacao.getValorTransacao()));
        preencherTransacaoSaqueConcluido(conta, transacao);
    }

    private boolean validarSaldoCliente(BigDecimal valorTransacao, BigDecimal saldoClienteConta) {
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

    //MOVER PARA CONSTRUTOR
    private void preencherTransacaoTransferenciaConcluida(Conta origem, Conta destino, Transacao transacao) {
        transacao.setContaOrigem(origem);
        transacao.setContaDestino(destino);
        transacao.setStatusTransacao(StatusTransacaoEnum.CONCLUIDA);
        transacao.setTipoTransacaoFinanceira(TipoTransacaoFinEnum.TRANSFERENCIA);
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setTipoDespesa(TipoDespesaEnum.OUTROS);
    }

    //MOVER PARA CONSTRUTOR
    private void preencherTransacaoSaqueConcluido(Conta origem, Transacao transacao) {
        transacao.setContaOrigem(origem);
        transacao.setStatusTransacao(StatusTransacaoEnum.CONCLUIDA);
        transacao.setTipoTransacaoFinanceira(TipoTransacaoFinEnum.RETIRADA);
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setTipoDespesa(TipoDespesaEnum.OUTROS);
    }

    //MOVER PARA CONSTRUTOR
    private void preencherTransacaoDespositoConcluida(Conta origem, Transacao transacao) {
        transacao.setContaOrigem(origem);
        transacao.setStatusTransacao(StatusTransacaoEnum.CONCLUIDA);
        transacao.setTipoTransacaoFinanceira(TipoTransacaoFinEnum.DEPOSITO);
        transacao.setDataTransacao(LocalDateTime.now());
    }

    //MOVER PARA CONSTRUTOR
    private void preencherTransacaoComErro(Conta origem, Transacao transacao) {
        transacao.setContaOrigem(origem);
        transacao.setStatusTransacao(StatusTransacaoEnum.PENDENTE);
        transacao.setTipoTransacaoFinanceira(TipoTransacaoFinEnum.DEPOSITO);
        transacao.setDataTransacao(LocalDateTime.now());
    }


    public Optional<?> converterCambio(String moedaOrigem, String moedaDestino, BigDecimal valor) {
        return converteCambioService.converterCambio(moedaOrigem, moedaDestino, valor);
    }
}
