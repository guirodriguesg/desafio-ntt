package nttdata.bank.controllers.conta;

import nttdata.bank.controllers.conta.requests.ContaRequest;
import nttdata.bank.controllers.conta.responses.ContaResponse;
import nttdata.bank.mappers.conta.ContaMapper;
import nttdata.bank.service.conta.ContaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*") //ALTERAR PARA ORIGIN PERMITIDO
@RequestMapping("/api/v1/conta")
public class ContaController {

    private static final Logger log = LoggerFactory.getLogger(ContaController.class);

    private final ContaService contaService;
    private final ContaMapper contaMapper;

    public ContaController(ContaService contaService, ContaMapper contaMapper) {
        this.contaService = contaService;
        this.contaMapper = contaMapper;
    }

    @GetMapping
    private ResponseEntity<List<ContaResponse>> buscarContaPorCliente() {
        log.info("Buscando conta");
        return ResponseEntity.ok(contaService.getAllContas());
    }

    @GetMapping("/{id}")
    private ResponseEntity<List<ContaResponse>> buscarContaPorCliente(@PathVariable(value = "id") Long idCliente) {
        log.info("Buscando conta");
        return ResponseEntity.ok(contaService.getContaByIdCliente(idCliente));
    }

    @PostMapping
    private ResponseEntity<ContaResponse> criarConta(@RequestBody ContaRequest contaRequest) {
        log.info("Send deposit");
        return contaService.createConta(contaRequest).map(contaMapper::toContaResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    private ResponseEntity<ContaResponse> atualizarConta(@PathVariable(value = "id") Long idConta, @RequestBody ContaRequest contaRequest) {
        log.info("Atualizando conta");
        return contaService.updateConta(idConta, contaRequest).map(contaMapper::toContaResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    private void deletarConta(@PathVariable(value = "id") Long idConta) {
        log.info("Deletando conta");
        contaService.deleteConta(idConta);
    }

}
