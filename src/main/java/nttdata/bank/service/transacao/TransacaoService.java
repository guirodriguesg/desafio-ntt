package nttdata.bank.service.transacao;

import nttdata.bank.domain.dto.transacao.TransacaoDTO;
import nttdata.bank.domain.entities.conta.Conta;
import nttdata.bank.domain.entities.transacao.StatusTransacaoEnum;
import nttdata.bank.domain.entities.transacao.TipoDespesaEnum;
import nttdata.bank.domain.entities.transacao.TipoTransacaoFinEnum;
import nttdata.bank.domain.entities.transacao.Transacao;
import nttdata.bank.repository.transacao.TransacaoRepository;
import nttdata.bank.service.GraficoService;
import nttdata.bank.service.conta.ContaService;
import nttdata.bank.service.ports.ConverteCambioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    private static final Logger log = LoggerFactory.getLogger(TransacaoService.class);

    private static final String MOEDA_BRL = "BRL";

    private final TransacaoRepository transacaoRepository;
    private final ContaService contaService;
    private final ConverteCambioService converteCambioService;
    private final RelatorioTransacaoService relatorioTransacaoService;
    private final GraficoService graficoService;

    public TransacaoService(TransacaoRepository transacaoRepository, ContaService ContaService, ConverteCambioService converteCambioService, RelatorioTransacaoService relatorioTransacaoService, GraficoService graficoService) {
        this.transacaoRepository = transacaoRepository;
        this.contaService = ContaService;
        this.converteCambioService = converteCambioService;
        this.relatorioTransacaoService = relatorioTransacaoService;
        this.graficoService = graficoService;
    }


    public Optional<Transacao> realizarDeposito(Transacao transacao) {
        log.info("Realizando depósito");
        Optional<Conta> conta = buscarContaPorId(transacao.getIdContaOrigem());
        verificarSeContaExite(conta);
        conta.ifPresent(contaReturn -> executarDeposito(contaReturn, transacao));

        return Optional.of(transacaoRepository.save(transacao));
    }

    private void executarDeposito(Conta conta, Transacao transacao) {
        conta.setSaldo(conta.getSaldo().add(transacao.getValorTransacao()));
        conta = contaService.salvar(conta);
        BigDecimal cotacao = (BigDecimal) buscarTaxaCambio(MOEDA_BRL).get();
        preencherTransacaoDespositoConcluida(conta, transacao, cotacao);
    }

    public Optional<Transacao> realizarSaque(Transacao transacao) {
        log.info("Realizando saque");
        Optional<Conta> conta = buscarContaPorId(transacao.getIdContaOrigem());
        verificarSeContaExite(conta);

        conta.ifPresent(contaReturn -> {
            verificarSeSaldoSuficiente(transacao, contaReturn);
            executarSaque(contaReturn, transacao);
        });

        return Optional.of(transacaoRepository.save(transacao));
    }

    private void executarSaque(Conta conta, Transacao transacao) {
        conta.setSaldo(conta.getSaldo().subtract(transacao.getValorTransacao()));
        BigDecimal cotacao = (BigDecimal) buscarTaxaCambio(MOEDA_BRL).get();
        conta = contaService.salvar(conta);
        preencherTransacaoSaqueConcluido(conta, transacao, cotacao);
    }

    public Optional<Transacao> realizarTransferencia(Transacao transacao) {
        log.info("Realizando transferencia da conta com id: {}", transacao.getIdContaOrigem());
        Optional<Conta> contaOrigem = buscarContaPorId(transacao.getIdContaOrigem());
        verificarSeContaExite(contaOrigem);
        contaOrigem.ifPresent(conta -> verificarSeSaldoSuficiente(transacao, conta));
        Optional<Conta> contaDestino = buscarContaPorId(transacao.getIdContaDestino());
        verificarSeContaExite(contaDestino);

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

        contaOrigem.ifPresent(conta -> {
            verificarSeSaldoSuficiente(transacao, conta);
            executarPagamento(conta, transacao);
        });

        return Optional.of(transacaoRepository.save(transacao));
    }

    private void executarPagamento(Conta conta, Transacao transacao) {
        conta.setSaldo(conta.getSaldo().subtract(transacao.getValorTransacao()));
        BigDecimal cotacao = (BigDecimal) buscarTaxaCambio(MOEDA_BRL).get();
        conta = contaService.salvar(conta);
        preencherTransacaoSaqueConcluido(conta, transacao, cotacao);
    }


    private void executarTransferencia(Conta origem, Conta destino, Transacao transacao) {
        AtomicReference<BigDecimal> cotacao = new AtomicReference<>();
                origem.setSaldo(origem.getSaldo().subtract(transacao.getValorTransacao()));
        destino.setSaldo(destino.getSaldo().add(transacao.getValorTransacao()));
        origem = contaService.salvar(origem);
        destino = contaService.salvar(destino);
        buscarTaxaCambio(MOEDA_BRL).ifPresent(cotacaoReturn -> cotacao.set((BigDecimal) cotacaoReturn));
        preencherTransacaoTransferenciaConcluida(origem, destino, transacao, cotacao.get());
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

    public Optional<List<Transacao>> getTransacoesClientePorIdCliente(Long idCliente) {
    log.info("Buscando transacoes do cliente com id: {}", idCliente);
    return transacaoRepository.findByIdClienteContaOrigem(idCliente);
    }

    public void gerarRelatorioTransacao(List<TransacaoDTO> transacaoDTOList, OutputStream outputStream) {
        try {
            log.info("Gerando relatório de transações");
            relatorioTransacaoService.gerarRelatorioTransacaoPdf(transacaoDTOList, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ByteArrayOutputStream graficoDespesas(Long idCliente) {
        log.info("Buscando despesas do usuario com id {}", idCliente);
        List<Transacao> transacoes = transacaoRepository.findByIdClienteContaOrigem(idCliente).orElse(null);
        assert transacoes != null;
        if(transacoes.isEmpty()){
            throw new RuntimeException("Nenhuma despesa encontrada para o cliente com id: " + idCliente);
        }
        log.info("Despesas encontradas: {}", transacoes.size());
        Map<TipoDespesaEnum, BigDecimal> gastosPorCategoria = transacoes.stream()
                .peek(transacao -> {
                    if(transacao.getTipoDespesa() == null){
                        transacao.setTipoDespesa(TipoDespesaEnum.OUTROS);
                    }
                })
                .collect(Collectors.groupingBy(
                        Transacao::getTipoDespesa,
                        Collectors.mapping(
                                Transacao::getValorTransacao,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
        return graficoService.gerarGrafico(new GraficoDespesaService(), Optional.of(gastosPorCategoria));
    }
}
