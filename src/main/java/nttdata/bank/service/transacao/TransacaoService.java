package nttdata.bank.service.transacao;

import nttdata.bank.domain.entities.conta.Conta;
import nttdata.bank.domain.entities.transacao.StatusTransacaoEnum;
import nttdata.bank.domain.entities.transacao.TipoDespesaEnum;
import nttdata.bank.domain.entities.transacao.TipoTransacaoFinEnum;
import nttdata.bank.domain.entities.transacao.Transacao;
import nttdata.bank.repository.transacao.TransacaoRepository;
import nttdata.bank.service.conta.ContaService;
import nttdata.bank.service.ports.ConverteCambioService;
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
    private static final String MOEDA_BRL = "BRL";
    private static final String MOEDA_USA = "USD";

    private final TransacaoRepository transacaoRepository;
    private final ContaService contaService;
    private final ConverteCambioService converteCambioService;

    public TransacaoService(TransacaoRepository transacaoRepository, ContaService ContaService, ConverteCambioService converteCambioService) {
        this.transacaoRepository = transacaoRepository;
        this.contaService = ContaService;
        this.converteCambioService = converteCambioService;
    }


    public Optional<Transacao> realizarDeposito(Transacao transacao) {
        log.info("Realizando depósito");
        Optional<Conta> conta = buscarContaPorId(transacao.getIdContaOrigem());
        verificarSeContaExite(conta);
        executarDeposito(conta.get(), transacao);
        return Optional.of(transacaoRepository.save(transacao));
    }

    private void executarDeposito(Conta conta, Transacao transacao) {
        conta.setSaldo(conta.getSaldo().add(transacao.getValorTransacao()));
        BigDecimal cotacao = (BigDecimal) buscarTaxaCambio(MOEDA_BRL).get();
        preencherTransacaoDespositoConcluida(conta, transacao, cotacao);
    }

    public Optional<Transacao> realizarSaque(Transacao transacao) {
        log.info("Realizando saque");
        Optional<Conta> conta = buscarContaPorId(transacao.getIdContaOrigem());
        verificarSeContaExite(conta);

        verificarSeSaldoSuficiente(transacao, conta.get());

        executarSaque(conta.get(), transacao);
        return Optional.of(transacaoRepository.save(transacao));
    }

    private void executarSaque(Conta conta, Transacao transacao) {
        conta.setSaldo(conta.getSaldo().subtract(transacao.getValorTransacao()));
        BigDecimal cotacao = (BigDecimal) buscarTaxaCambio(MOEDA_BRL).get();
        preencherTransacaoSaqueConcluido(conta, transacao, cotacao);
    }

    public Optional<Transacao> realizarTransferencia(Transacao transacao) {
        log.info("Realizando transferencia da conta com id: {}", transacao.getIdContaOrigem());
        Optional<Conta> contaOrigem = buscarContaPorId(transacao.getIdContaOrigem());
        verificarSeContaExite(contaOrigem);

        verificarSeSaldoSuficiente(transacao, contaOrigem.get());

        Optional<Conta> contaDestino = buscarContaPorId(transacao.getIdContaDestino());
        verificarSeContaExite(contaOrigem);

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
        verificarSeContaExite(contaOrigem);

        verificarSeSaldoSuficiente(transacao, contaOrigem.get());

        executarPagamento(contaOrigem.get(), transacao);
        return Optional.of(transacaoRepository.save(transacao));
    }

    private void executarPagamento(Conta conta, Transacao transacao) {
        conta.setSaldo(conta.getSaldo().subtract(transacao.getValorTransacao()));
        BigDecimal cotacao = (BigDecimal) buscarTaxaCambio(MOEDA_BRL).get();
        preencherTransacaoSaqueConcluido(conta, transacao, cotacao);
    }


    private void executarTransferencia(Conta origem, Conta destino, Transacao transacao) {
        origem.setSaldo(origem.getSaldo().subtract(transacao.getValorTransacao()));
        destino.setSaldo(destino.getSaldo().add(transacao.getValorTransacao()));
        BigDecimal cotacao = (BigDecimal) buscarTaxaCambio(MOEDA_BRL).get();
        preencherTransacaoTransferenciaConcluida(origem, destino, transacao, cotacao);
    }

    private Optional<Conta> buscarContaPorId(Long id) {
        return contaService.buscarContaPorId(id);
    }

    public Optional<?> converterCambio(String moedaOrigem, String moedaDestino, BigDecimal valor) {
        return converteCambioService.converterCambio(moedaOrigem, moedaDestino, valor);
    }

    public Optional<?> buscarTaxaCambio(String moedaOrigem) {
        return converteCambioService.buscarTaxaCambio(moedaOrigem);
    }

    private void verificarSeSaldoSuficiente(Transacao transacao, Conta conta) {
        transacao.saldoEhInsuficiente(conta.getSaldo());
    }

    private void verificarSeContaExite(Optional<Conta> conta) {
        contaService.contaNaoExiste(conta);
    }

    //MOVER PARA CONSTRUTOR
    private void preencherTransacaoTransferenciaConcluida(Conta origem, Conta destino, Transacao transacao, BigDecimal cotacao) {
        transacao.setContaOrigem(origem);
        transacao.setContaDestino(destino);
        transacao.setStatusTransacao(StatusTransacaoEnum.CONCLUIDA);
        transacao.setTipoTransacaoFinanceira(TipoTransacaoFinEnum.TRANSFERENCIA);
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setTipoDespesa(TipoDespesaEnum.OUTROS);
        transacao.setTaxaCambio(cotacao);
    }

    //MOVER PARA CONSTRUTOR
    private void preencherTransacaoSaqueConcluido(Conta origem, Transacao transacao, BigDecimal cotacao) {
        transacao.setContaOrigem(origem);
        transacao.setStatusTransacao(StatusTransacaoEnum.CONCLUIDA);
        transacao.setTipoTransacaoFinanceira(TipoTransacaoFinEnum.RETIRADA);
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setTipoDespesa(TipoDespesaEnum.OUTROS);
        transacao.setTaxaCambio(cotacao);
    }

    //MOVER PARA CONSTRUTOR
    private void preencherTransacaoDespositoConcluida(Conta origem, Transacao transacao, BigDecimal cotacao) {
        transacao.setContaOrigem(origem);
        transacao.setStatusTransacao(StatusTransacaoEnum.CONCLUIDA);
        transacao.setTipoTransacaoFinanceira(TipoTransacaoFinEnum.DEPOSITO);
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setTaxaCambio(cotacao);
    }

    //MOVER PARA CONSTRUTOR
    private void preencherTransacaoComErro(Conta origem, Transacao transacao) {
        transacao.setContaOrigem(origem);
        transacao.setStatusTransacao(StatusTransacaoEnum.PENDENTE);
        transacao.setTipoTransacaoFinanceira(TipoTransacaoFinEnum.DEPOSITO);
        transacao.setDataTransacao(LocalDateTime.now());
    }
}
