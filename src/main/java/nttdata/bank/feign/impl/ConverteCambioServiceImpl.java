package nttdata.bank.feign.impl;

import nttdata.bank.exceptions.cambio.CambioBusinessException;
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
            if(moedaOrigem.equals(moedaDestino)){
                log.warn("Moedas de origem e destino iguais, nao e necessario conversao");
                return Optional.of(valor);
            }

            if(valor.compareTo(BigDecimal.ZERO) <= 0){
                log.warn("Valor invalido para conversao");
                throw new CambioBusinessException("Valor invalido para conversao");
            }

            Optional<CambioResponse> cambioResponse = converteCambioClient.getTodasCotacoes();
            if(cambioResponse.isPresent()){
                log.info("Cotacoes recebidas com sucesso, realizando conversao de {} para {} ...", moedaOrigem, moedaDestino);

                BigDecimal taxaOrigem = cambioResponse.get().getCotacaoMoeda(moedaOrigem);
                BigDecimal taxaDestino = cambioResponse.get().getCotacaoMoeda(moedaDestino);
                if (cotacaoValida(taxaOrigem) && cotacaoValida(taxaDestino)) {
                    BigDecimal valorConvertido = valor.multiply(taxaDestino).divide(taxaOrigem, 2, RoundingMode.HALF_UP);
                    return Optional.of(valorConvertido);
                } else {
                    log.warn("Nao foi possivel converter a moeda: Cotacao invalida!");
                    throw new CambioBusinessException("Nao foi possivel converter a moeda: Cotacao invalida!");
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Erro ao chamar api para conversao de cambio...");
            throw new RuntimeException(e);
        }
    }

    private boolean cotacaoValida(BigDecimal cotacao) {
        return cotacao != null && cotacao.compareTo(BigDecimal.ZERO) > 0;
    }

}
