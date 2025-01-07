package nttdata.bank.service.conta;

import nttdata.bank.controllers.conta.requests.ContaRequest;
import nttdata.bank.domain.entities.conta.Conta;
import nttdata.bank.domain.entities.usuario.Usuario;
import nttdata.bank.handlers.ContaException;
import nttdata.bank.repository.conta.ContaRepository;
import nttdata.bank.service.ports.ClienteExternoService;
import nttdata.bank.service.usuario.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static nttdata.bank.utils.ConstatesUtils.*;

@Service
public class ContaService {

    private static final Logger log = LoggerFactory.getLogger(ContaService.class);

    private final ContaRepository contaRepository;
    private final UsuarioService usuarioService;
    private final ClienteExternoService clienteExternoService;

    public ContaService(ContaRepository contaRepository, UsuarioService usuarioService, ClienteExternoService clienteExternoService) {
        this.contaRepository = contaRepository;
        this.usuarioService = usuarioService;
        this.clienteExternoService = clienteExternoService;
    }

    public Page<Conta> getAllContas(Pageable pageable) {
        return contaRepository.findAll(pageable);
    }

    public List<Conta> getContaByIdCliente(Long idCliente) {
        return contaRepository.findContaByIdUsuario(idCliente).orElse(null);
    }

    public Optional<Conta> createConta(Conta conta, Long idUsuario) {
        Optional<Usuario> usuario = usuarioService.getUserById(idUsuario);
        if (usuario.isEmpty()) {
            log.error("Necessario um usuario ativo para criar uma conta");
            throw new ContaException("Necessario um usuario ativo para criar uma conta", BAD_REQUEST);
        }

        conta.setUsuario(usuario.get());

        try{
            return Optional.of(contaRepository.save(conta));
        } catch (Exception e) {
            if(e.getMessage().contains("conta_uk_cod_agencia_cod_conta")) {
                log.error("Conta já existe");
                throw new ContaException("Conta já existe", BAD_REQUEST);
            }
            log.error("Erro ao criar conta");
            throw new ContaException("Erro ao criar conta", INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<Conta> updateConta(Long idConta, ContaRequest contaRequest) {
        log.info("Atualizando conta com id: {}", idConta);
        Optional<Conta> existingContaOpt = contaRepository.findById(idConta);
        if (existingContaOpt.isEmpty()) {
            log.warn("Conta com id {} não encontrada", idConta);
            throw new ContaException("Conta não encontrada", NOT_FOUND);
        }
        Conta existingConta = existingContaOpt.get();
        existingConta.setCodAgencia(contaRequest.digitoAgencia());
        existingConta.setDigitoAgencia(contaRequest.digitoAgencia());
        existingConta.setCodConta(contaRequest.codigoConta());
        existingConta.setDigitoConta(contaRequest.digitoConta());

        return Optional.of(contaRepository.save(existingConta));
    }

    public void deleteConta(Long id) {
        log.info("Deletando conta por id {}", id);
        contaRepository.deleteById(id);
    }

    public Optional<Conta> buscarContaPorId(Long idConta) {
        log.info("Buscando conta por agencia e conta");
        return contaRepository.findById(idConta);
    }

    public void contaNaoExiste(Optional<Conta> conta) {
        if (conta.isEmpty()) {
            throw new ContaException("Conta não encontrada", NOT_FOUND);
        }
    }

    public BigDecimal saldoAtualbyMockApi(String id) {
        log.info("Buscando saldo atual cliente externo com id {}", id);
        if (id == null) {
            log.warn("Id do cliente externo não pode ser nulo");
            throw new ContaException("Id do cliente externo não pode ser nulo", BAD_REQUEST);
        }
        BigDecimal saldo = (BigDecimal) clienteExternoService.buscarSaldoClienteExterno(id).orElse(null);
        if (saldo == null) {
            log.warn("Saldo não encontrado para o cliente {}", id);
            throw new ContaException("Saldo não encontrado", NOT_FOUND);
        }

        return saldo.setScale(2, RoundingMode.HALF_UP);
    }

    public Conta salvar(Conta conta) {
        log.info("Salvando conta");
        return contaRepository.save(conta);
    }
}
