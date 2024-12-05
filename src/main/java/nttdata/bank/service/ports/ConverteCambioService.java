package nttdata.bank.service.ports;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public interface ConverteCambioService {

    Optional<?> buscarTaxaCambio(String moedaOrigem);

    Optional<?> converterCambio(String moedaOrigem, String moedaDestino, BigDecimal valor);
}
