package nttdata.bank.feign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import static nttdata.bank.utils.ConstatesUtils.HEADER_APIKEY_MOCKAPI;

@Configuration
public class FeignInterceptorConfig implements RequestInterceptor {

    @Value("${api.key.mockapi}")
    private String apiKeyMockApiValue;

    @Override
    public void apply(RequestTemplate template) {
        template.header(HEADER_APIKEY_MOCKAPI, this.apiKeyMockApiValue);
    }
}
