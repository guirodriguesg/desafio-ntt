package nttdata.bank.feign.clients;


import nttdata.bank.feign.response.ClienteExterno;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@FeignClient(name = "cliente-externo-api", url = " https://api.mockapi.com/api/v1")
public interface ContaExternaClient {

    @GetMapping("/cliente-externo")
    Optional<ClienteExterno> getContaClieneExterno();
}
