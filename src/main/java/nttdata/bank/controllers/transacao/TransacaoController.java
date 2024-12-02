package nttdata.bank.controllers.transacao;

import nttdata.bank.controllers.transacao.requests.TransacaoRequest;
import nttdata.bank.controllers.transacao.responses.TransacaoResponse;
import nttdata.bank.mappers.transacao.TransacaoMapper;
import nttdata.bank.service.transacao.TransacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@CrossOrigin(origins = "*") //ALTERAR PARA ORIGIN PERMITIDO
@RequestMapping("/api/v1/transacao")
public class TransacaoController {

    private static final Logger log = LoggerFactory.getLogger(TransacaoController.class);
    private final TransacaoService transacaoService;
    private final TransacaoMapper transacaoMapper;

    public TransacaoController(TransacaoService transacaoService, TransacaoMapper transacaoMapper) {
        this.transacaoService = transacaoService;
        this.transacaoMapper = transacaoMapper;
    }

    @PostMapping("/deposito")
    private ResponseEntity<TransacaoResponse> realizarDeposito(@RequestBody TransacaoRequest transacaoRequest) {
        log.info("Realizando depósito");
        return transacaoService.realizarDeposito(transacaoMapper.toTransacao(transacaoRequest))
                .map(transacaoMapper::toTransacaoResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/transferencia")
    private ResponseEntity<TransacaoResponse> realizarTransferencia(@RequestBody TransacaoRequest transacaoRequest) {
        log.info("Realizando transferência");
        return transacaoService.realizarTransferencia(transacaoMapper.toTransacao(transacaoRequest))
                .map(transacaoMapper::toTransacaoResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
    @PostMapping("/saque")
    private ResponseEntity<TransacaoResponse> realizarSaque(@RequestBody TransacaoRequest transacaoRequest) {
        log.info("Realizando saque");
        return transacaoService.realizarSaque(transacaoMapper.toTransacao(transacaoRequest))
                .map(transacaoMapper::toTransacaoResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/pagamento")
    private ResponseEntity<TransacaoResponse> realizarPagamento(@RequestBody TransacaoRequest transacaoRequest) {
        log.info("Realizando pagamento transferência");
        return transacaoService.realizarPagamento(transacaoMapper.toTransacao(transacaoRequest))
                .map(transacaoMapper::toTransacaoResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/converte-cambio")
    public ResponseEntity<?> converterCambio(@RequestParam String moedaOrigem,
                                             @RequestParam String moedaDestino, @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(transacaoService.converterCambio(moedaOrigem, moedaDestino, valor));
    }


}
