package nttdata.bank.feign.clients;


import nttdata.bank.feign.config.FeignInterceptorConfig;
import nttdata.bank.feign.response.mockapi.ClienteExterno;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@FeignClient(name = "cliente-externo-api", url = "${cliente.externo.api.url}", configuration = FeignInterceptorConfig.class)
public interface ContaExternaClient {

    @GetMapping("/cliente-externo")
    Optional<ClienteExterno> getContaClieneExterno();

    @GetMapping("/cliente-externo/saldo/{id}")
    Optional<Map<String, BigDecimal>> getSaldoClieneExterno(@PathVariable(name = "id") String id);
}
