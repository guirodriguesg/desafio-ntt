package nttdata.bank.feign.clients;


import nttdata.bank.feign.config.FeignInterceptorConfig;
import nttdata.bank.feign.response.mockapi.ClienteExterno;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@FeignClient(name = "cliente-externo-api", url = "https://api.mockapi.com/api/v1",
configuration = FeignInterceptorConfig.class)//TRANSFERIR PARA O APPLICATION.PROPERTIES
public interface ContaExternaClient {

    @GetMapping("/cliente-externo")
    Optional<ClienteExterno> getContaClieneExterno();
}
