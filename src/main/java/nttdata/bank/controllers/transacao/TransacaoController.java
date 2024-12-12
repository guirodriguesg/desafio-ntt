package nttdata.bank.controllers.transacao;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import nttdata.bank.controllers.transacao.requests.TransacaoRequest;
import nttdata.bank.controllers.transacao.responses.TransacaoResponse;
import nttdata.bank.mappers.transacao.TransacaoMapper;
import nttdata.bank.service.transacao.TransacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@CrossOrigin(origins = "*") //ALTERAR PARA ORIGIN PERMITIDO
@RequestMapping("/api/v1/transacao")
@SecurityRequirement(name = "bearer-key")
public class TransacaoController {

    private static final Logger log = LoggerFactory.getLogger(TransacaoController.class);

    private static final String DATE_FORMAT = "ddMMyyyy_HHmmss";

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

    @GetMapping(value = "/relatorio-transacao/{idCliente}")

    public ResponseEntity<?> gerarRelatorioTransacao(@PathVariable(name = "idCliente") Long idCliente) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        transacaoService.gerarRelatorioTransacao(
                transacaoMapper.toListaTransacaoDTO(transacaoService.getTransacoesClientePorIdCliente(idCliente).orElse(null)), baos);
        byte[] pdfBytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "relatorio-transacao_".concat(dataHora).concat(".pdf"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/relatorio-despesas/{idCliente}")
    public ResponseEntity<?> relatorioDespesas(@PathVariable(value = "idCliente") Long idCliente) {
        ByteArrayOutputStream baos = transacaoService.graficoDespesas(idCliente);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"grafico_despesas.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(baos.toByteArray());
    }

}
