package nttdata.bank.feign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignInterceptorConfig implements RequestInterceptor {

//    @Value("${api.key.mockapi}")
    private String apiKeyMockApiValue = "5a97079c885145ecabedfe5372d6b5aa";//MUDAR PARA O APPLICATION.PROPERTIES
    private static final String APIKEY_MOCKAPI = "x-api-key";

    @Override
    public void apply(RequestTemplate template) {
        template.header(APIKEY_MOCKAPI, this.apiKeyMockApiValue);
    }
}
