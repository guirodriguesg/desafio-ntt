package nttdata.bank.service.conta;

import nttdata.bank.controllers.conta.requests.ContaRequest;
import nttdata.bank.controllers.conta.responses.ContaResponse;
import nttdata.bank.domain.entities.conta.Conta;
import nttdata.bank.domain.entities.usuario.Usuario;
import nttdata.bank.mappers.conta.ContaMapper;
import nttdata.bank.repository.conta.ContaRepository;
import nttdata.bank.service.usuario.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContaService {

    private static final Logger log = LoggerFactory.getLogger(ContaService.class);

    private final ContaRepository contaRepository;
    private final UsuarioService usuarioService;
    private final ContaMapper contaMapper;

    public ContaService(ContaRepository contaRepository, UsuarioService usuarioService, ContaMapper contaMapper) {
        this.contaRepository = contaRepository;
        this.usuarioService = usuarioService;
        this.contaMapper = contaMapper;
    }


    public List<ContaResponse> getAllContas() {
        log.info("Buscando todas as contas");
        return contaRepository.findAll().stream().map(contaMapper::toContaResponse).collect(Collectors.toList());
    }

    public List<ContaResponse> getContaByIdCliente(Long idCliente) {
        log.info("Buscando conta com id: {}", idCliente);
        return contaRepository.findByIdUsuario(idCliente).map(contaMapper::toUsuarioResponseList).orElse(null);
    }

    public Optional<Conta> createConta(ContaRequest contaRequest) {
        log.info("Criando conta");
        Optional<Usuario> usuario = usuarioService.getUserById(contaRequest.getIdUsuario());
        if (usuario.isEmpty()) {
            log.error("Necessario um usuario ativo para criar uma conta");
            return Optional.empty();
        }

        Conta conta = contaMapper.toConta(contaRequest);
        conta.setUsuario(usuario.get());

        return Optional.of(contaRepository.save(conta));
    }

    public Optional<Conta> updateConta(Long idConta, ContaRequest contaRequest) {
        log.info("Atualizando conta com id: {}", idConta);
        Optional<Conta> existingContaOpt = contaRepository.findById(idConta);
        if (existingContaOpt.isEmpty()) {
            log.warn("Conta com id {} não encontrada", idConta);
            return Optional.empty();
        }
        Conta existingConta = existingContaOpt.get();
        existingConta.setCodAgencia(contaRequest.getDigitoAgencia());
        existingConta.setDigitoAgencia(contaRequest.getDigitoAgencia());
        existingConta.setCodConta(contaRequest.getCodigoConta());
        existingConta.setDigitoConta(contaRequest.getDigitoConta());

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

    public void atualizarConta(Conta conta) {
        log.info("Atualizando conta");
        contaRepository.save(conta);
    }

    public boolean validarConta(Long idConta) {
        log.info("Validando conta");
        return contaRepository.existsById(idConta);
    }
}
