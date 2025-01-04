package nttdata.bank.service.conta;


import nttdata.bank.controllers.conta.requests.ContaRequest;
import nttdata.bank.controllers.conta.responses.ContaResponse;
import nttdata.bank.domain.entities.conta.Conta;
import nttdata.bank.domain.entities.usuario.Usuario;
import nttdata.bank.mappers.conta.ContaMapper;
import nttdata.bank.repository.conta.ContaRepository;
import nttdata.bank.service.ports.ClienteExternoService;
import nttdata.bank.service.usuario.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContaServiceTest {

    @Mock
    private ClienteExternoService clienteExternoService;

    @Mock
    private ContaMapper contaMapper;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ContaService contaService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Deve retornar todas as contas")
    void getAllContasTest() {
        List<Conta> contas = List.of(new Conta());
        List<ContaResponse> contaResponses = getListaContaResponse();

        when(contaRepository.findAll()).thenReturn(contas);
        when(contaMapper.toContaResponse(any(Conta.class))).thenReturn(contaResponses.get(0));

        List<ContaResponse> result = contaService.getAllContas();

        assertEquals(contaResponses, result);
        verify(contaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar conta por id cliente")
    void getContaByIdCliente() {
        Long idCliente = 1L;
        List<Conta> contas = List.of(new Conta());
        List<ContaResponse> contaResponses = getListaContaResponse();

        when(contaRepository.findByIdUsuario(idCliente)).thenReturn(Optional.of(contas));
        when(contaMapper.toUsuarioResponseList(contas)).thenReturn(contaResponses);

        List<ContaResponse> result = contaService.getContaByIdCliente(idCliente);

        assertEquals(contaResponses, result);
        verify(contaRepository, times(1)).findByIdUsuario(idCliente);
    }

    @Test
    @DisplayName("Deve retornar erro conta nao encontrada por id cliente")
    void getContaByIdCliente_ContaNotFound() {
        Long idCliente = 1L;

        when(contaRepository.findByIdUsuario(idCliente)).thenReturn(Optional.empty());

        List<ContaResponse> result = contaService.getContaByIdCliente(idCliente);

        assertNull(result);
        verify(contaRepository, times(1)).findByIdUsuario(idCliente);
    }

    @Test
    @DisplayName("Deve criar uma conta")
    void createConta_Success() {
        ContaRequest contaRequest = getContaRequest();
        Usuario usuario = new Usuario();
        Conta conta = new Conta();

        when(usuarioService.getUserById(contaRequest.idUsuario())).thenReturn(Optional.of(usuario));
        when(contaMapper.toConta(contaRequest)).thenReturn(conta);
        when(contaRepository.save(conta)).thenReturn(conta);

        Optional<Conta> result = contaService.createConta(contaRequest);

        assertTrue(result.isPresent());
        assertEquals(conta, result.get());
        verify(contaRepository, times(1)).save(conta);
    }

    @Test
    @DisplayName("Deve retornar erro ao criar uma conta")
    void createConta_UserNotFound() {
        ContaRequest contaRequest = getContaRequest();

        when(usuarioService.getUserById(contaRequest.idUsuario())).thenReturn(Optional.empty());

        Optional<Conta> result = contaService.createConta(contaRequest);

        assertFalse(result.isPresent());
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    @DisplayName("Deve atualizar uma conta")
    void updateConta_Success() {
        Long idConta = 1L;
        ContaRequest contaRequest = getContaRequest();
        Conta existingConta = new Conta();

        when(contaRepository.findById(idConta)).thenReturn(Optional.of(existingConta));
        when(contaRepository.save(existingConta)).thenReturn(existingConta);

        Optional<Conta> result = contaService.updateConta(idConta, contaRequest);

        assertTrue(result.isPresent());
        assertEquals(existingConta, result.get());
        verify(contaRepository, times(1)).save(existingConta);
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar uma conta")
    void updateConta_ContaNotFound() {
        Long idConta = 1L;
        ContaRequest contaRequest = getContaRequest();

        when(contaRepository.findById(idConta)).thenReturn(Optional.empty());

        Optional<Conta> result = contaService.updateConta(idConta, contaRequest);

        assertFalse(result.isPresent());
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    @DisplayName("Deve deletar uma conta")
    void deleteConta_Success() {
        Long idConta = 1L;

        doNothing().when(contaRepository).deleteById(idConta);

        contaService.deleteConta(idConta);

        verify(contaRepository, times(1)).deleteById(idConta);
    }

    @Test
    @DisplayName("Deve retornar uma conta por id")
    void buscarContaPorId_ReturnsConta() {
        Long idConta = 1L;
        Conta conta = new Conta();

        when(contaRepository.findById(idConta)).thenReturn(Optional.of(conta));

        Optional<Conta> result = contaService.buscarContaPorId(idConta);

        assertTrue(result.isPresent());
        assertEquals(conta, result.get());
        verify(contaRepository, times(1)).findById(idConta);
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar uma conta por id")
    void buscarContaPorId_ContaNotFound() {
        Long idConta = 1L;

        when(contaRepository.findById(idConta)).thenReturn(Optional.empty());

        Optional<Conta> result = contaService.buscarContaPorId(idConta);

        assertFalse(result.isPresent());
        verify(contaRepository, times(1)).findById(idConta);
    }

    @Test
    @DisplayName("Deve validar se a conta exite")
    void validarConta_ReturnsTrue() {
        Long idConta = 1L;

        when(contaRepository.existsById(idConta)).thenReturn(true);

        boolean result = contaService.validarConta(idConta);

        assertTrue(result);
        verify(contaRepository, times(1)).existsById(idConta);
    }

    @Test
    @DisplayName("Deve validar se a conta nao existe")
    void validarConta_ReturnsFalse() {
        Long idConta = 1L;

        when(contaRepository.existsById(idConta)).thenReturn(false);

        boolean result = contaService.validarConta(idConta);

        assertFalse(result);
        verify(contaRepository, times(1)).existsById(idConta);
    }

    @Test
    @DisplayName("Deve retornar erro ao validar se a conta existe")
    void contaNaoExiste_ThrowsException() {
        Optional<Conta> conta = Optional.empty();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> contaService.contaNaoExiste(conta));

        assertEquals("Conta n\u00E3o encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar saldo MockApi")
    void saldoAtualbyMockApiTest() {
        String id = "1";
        BigDecimal saldo = BigDecimal.ONE;
        Mockito.<Optional<?>>when(clienteExternoService.buscarSaldoClienteExterno(id)).thenReturn(Optional.of(BigDecimal.ONE));

        BigDecimal result = contaService.saldoAtualbyMockApi(id);

        assertEquals(saldo.setScale(2, RoundingMode.HALF_UP), result);

        verify(clienteExternoService, times(1)).buscarSaldoClienteExterno(id);
    }

    @Test
    @DisplayName("Deve validar se id do cliente externo e nulo")
    void saldoAtualbyMockApi_IdIsNullTest() {
        String id = null;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> contaService.saldoAtualbyMockApi(id));

        assertEquals("Id do cliente externo n\u00E3o pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar saldo nao encontrado no MockApi")
     void saldoAtualbyMockApiNotFoundTest() {
        String id = "1";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> contaService.saldoAtualbyMockApi(id));

        assertEquals("Saldo n\u00E3o encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve salvar uma conta")
    void salvar_Success() {
        Conta conta = new Conta();

        when(contaRepository.save(conta)).thenReturn(conta);

        Conta result = contaService.salvar(conta);

        assertEquals(conta, result);
        verify(contaRepository, times(1)).save(conta);
    }

    public static ContaRequest getContaRequest(){
        return new ContaRequest(1L, 1L, "123", "00001", "999999", "2", "3", BigDecimal.ONE);
    }

    public static List<ContaResponse> getListaContaResponse(){
       return List.of(new ContaResponse(1L, 1L, "123", "00001", "999999", "2", "3", BigDecimal.ONE));
    }
}
