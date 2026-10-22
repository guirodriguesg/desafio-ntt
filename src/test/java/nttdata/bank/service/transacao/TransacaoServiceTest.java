package nttdata.bank.service.transacao;

import nttdata.bank.domain.entities.conta.Conta;
import nttdata.bank.domain.entities.transacao.StatusTransacaoEnum;
import nttdata.bank.domain.entities.transacao.TipoDespesaEnum;
import nttdata.bank.domain.entities.transacao.Transacao;
import nttdata.bank.repository.transacao.TransacaoRepository;
import nttdata.bank.service.GraficoService;
import nttdata.bank.service.conta.ContaService;
import nttdata.bank.service.ports.ConverteCambioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ContaService contaService;
    @Mock
    private GraficoService graficoService;

    @Mock
    private ConverteCambioService converteCambioService;

    @InjectMocks
    private TransacaoService transacaoService;

    private Transacao transacao;
    private Conta conta;

    @BeforeEach
    void setUp() {
        transacao = new Transacao();
        transacao.setIdContaOrigem(1L);
        transacao.setValorTransacao(BigDecimal.valueOf(100));

        conta = new Conta();
        conta.setSaldo(BigDecimal.valueOf(200));
    }

    @Test
    @DisplayName("Deve realizar um deposito")
    void realizarDeposito_Success() {
        when(contaService.buscarContaPorId(1L)).thenReturn(Optional.of(conta));
        when(transacaoRepository.save(any())).thenReturn(transacao);
        Mockito.<Optional<?>>when(converteCambioService.buscarTaxaCambio(any())).thenReturn(Optional.of(BigDecimal.ONE));

        Optional<Transacao> result = transacaoService.realizarDeposito(transacao);

        assertTrue(result.isPresent());
        assertEquals(transacao, result.get());
        assertEquals(BigDecimal.valueOf(300), conta.getSaldo());
        verify(transacaoRepository).save(transacao);
    }

    @Test
    @DisplayName("Deve realizar o saque")
    void realizarSaque_Success() {
        when(contaService.buscarContaPorId(1L)).thenReturn(Optional.of(conta));
        when(transacaoRepository.save(any())).thenReturn(transacao);
        Mockito.<Optional<?>>when(converteCambioService.buscarTaxaCambio(any())).thenReturn(Optional.of(BigDecimal.ONE));

        Optional<Transacao> result = transacaoService.realizarSaque(transacao);

        assertTrue(result.isPresent());
        assertEquals(transacao, result.get());
        assertEquals(BigDecimal.valueOf(100), conta.getSaldo());
        verify(transacaoRepository).save(transacao);
    }

    @Test
    @DisplayName("Deve realizar a transferencia entre contas")
    void realizarTransferencia_Success() {
        Transacao transacao = new Transacao();
        transacao.setIdContaOrigem(1L);
        transacao.setIdContaDestino(2L);
        transacao.setValorTransacao(BigDecimal.valueOf(100));

        Conta contaOrigem = new Conta();
        contaOrigem.setSaldo(BigDecimal.valueOf(1000));

        Conta contaDestino = new Conta();
        contaDestino.setSaldo(BigDecimal.valueOf(500));

        when(contaService.buscarContaPorId(1L)).thenReturn(Optional.of(contaOrigem));
        when(contaService.buscarContaPorId(2L)).thenReturn(Optional.of(contaDestino));
        Mockito.<Optional<?>>when(converteCambioService.buscarTaxaCambio("BRL")).thenReturn(Optional.of(BigDecimal.ONE));
        when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacao);

        Optional<Transacao> result = transacaoService.realizarTransferencia(transacao);

        assertTrue(result.isPresent());
        assertEquals(StatusTransacaoEnum.CONCLUIDA, result.get().getStatusTransacao());
        verify(contaService, times(1)).salvar(contaOrigem);
        verify(contaService, times(1)).salvar(contaDestino);
    }

    @Test
    @DisplayName("Deve realizar o pagamento de uma despesa")
    void realizarPagamento_Success() {
        when(contaService.buscarContaPorId(1L)).thenReturn(Optional.of(conta));
        when(transacaoRepository.save(any())).thenReturn(transacao);
        Mockito.<Optional<?>>when(converteCambioService.buscarTaxaCambio(any())).thenReturn(Optional.of(BigDecimal.ONE));

        Optional<Transacao> result = transacaoService.realizarPagamento(transacao);

        assertTrue(result.isPresent());
        assertEquals(transacao, result.get());
        assertEquals(BigDecimal.valueOf(100), conta.getSaldo());
        verify(transacaoRepository).save(transacao);
    }

    @Test
    @DisplayName("Deve retornar transacoes do cliente por id")
    void getTransacoesClientePorIdCliente_Success() {
        when(transacaoRepository.findByIdClienteContaOrigem(1L)).thenReturn(Optional.of(List.of(transacao)));

        Optional<List<Transacao>> result = transacaoService.getTransacoesClientePorIdCliente(1L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        verify(transacaoRepository).findByIdClienteContaOrigem(1L);
    }

    @Test
    @DisplayName("Deve retornar o grafico de despesas")
    void graficoDespesas_Success() {
        transacao.setTipoDespesa(TipoDespesaEnum.OUTROS);
        when(transacaoRepository.findByIdClienteContaOrigem(1L)).thenReturn(Optional.of(List.of(transacao)));
        when(graficoService.gerarGrafico(any(), any())).thenReturn(new ByteArrayOutputStream());

        ByteArrayOutputStream result = transacaoService.graficoDespesas(1L);

        assertNotNull(result);
        verify(transacaoRepository, times(1)).findByIdClienteContaOrigem(1L);
    }
    @Test
    @DisplayName("Deve retornar excecao ao tentar gerar grafico de despesas sem transacoes")
    void graficoDespesas_SemTransacoes() {
        transacao.setTipoDespesa(TipoDespesaEnum.OUTROS);
        when(transacaoRepository.findByIdClienteContaOrigem(1L)).thenReturn(Optional.of(Collections.emptyList()));

        Exception exception = assertThrows(RuntimeException.class, () -> transacaoService.graficoDespesas(1L));

        assertTrue(exception.getMessage().contains("Nenhuma despesa encontrada para o cliente com id"));
        verify(transacaoRepository, times(1)).findByIdClienteContaOrigem(1L);
    }
}