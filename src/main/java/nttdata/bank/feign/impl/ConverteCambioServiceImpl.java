package nttdata.bank.feign.impl;

import nttdata.bank.feign.clients.ConverteCambioClient;
import nttdata.bank.feign.response.cambio.CambioResponse;
import nttdata.bank.service.adapters.ConverteCambioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class ConverteCambioServiceImpl implements ConverteCambioService {

    private static final Logger log = LoggerFactory.getLogger(ConverteCambioServiceImpl.class);

    private final ConverteCambioClient converteCambioClient;

    public ConverteCambioServiceImpl(ConverteCambioClient converteCambioClient) {
        this.converteCambioClient = converteCambioClient;
    }

    @Override
    public Optional<?> converterCambio(String moedaOrigem, String moedaDestino, BigDecimal valor) {
        log.info("Chamando api externa para conversao de cambio...");
        try{
            Optional<CambioResponse> cambioResponse = converteCambioClient.getTodasCotacoes();
            if(cambioResponse.isPresent()){
                log.info("Cotacoes recebidas com sucesso, realizando conversao...");
                return Optional.of(cambioResponse.get().getCotacoes().get(moedaOrigem).multiply(valor));
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Erro ao chamar api para conversao de cambio...");
            throw new RuntimeException("Erro ao converter cambio");
        }
    }
}
