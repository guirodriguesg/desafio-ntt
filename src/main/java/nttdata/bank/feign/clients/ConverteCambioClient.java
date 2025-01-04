package nttdata.bank.feign.clients;

import nttdata.bank.feign.config.FeignInterceptorConverteCambioConfig;
import nttdata.bank.feign.response.cambio.CambioResponse;
import nttdata.bank.feign.response.mockapi.ClienteExterno;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Optional;

@FeignClient(name = "converter-cambio-api", url = "${exchangeratesapi.api.url}", configuration = FeignInterceptorConverteCambioConfig.class)
public interface ConverteCambioClient {

    @GetMapping("convert")
    Optional<CambioResponse> getCambioConvertido(@RequestParam("from") String moedaOriginal,
                                                   @RequestParam("to") String moedaConversao,
                                                   @RequestParam("amount") BigDecimal valorConversao);

    @GetMapping("latest")
    Optional<CambioResponse> getTodasCotacoes();

    @GetMapping("latest")
    Optional<ClienteExterno> getCotacoesPorMoedaBase(@RequestParam("base") String moedaBase, @RequestParam("symbols") String... moedasRetorno);
}
