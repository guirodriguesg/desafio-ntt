package nttdata.bank.feign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import static nttdata.bank.utils.ConstatesUtils.HEADER_ACCESS_KEY;

@Configuration
public class FeignInterceptorConverteCambioConfig implements RequestInterceptor {

    @Value("${api.key.exchangeratesapi}")
    private String accessKeyValue;

    @Override
    public void apply(RequestTemplate template) {
        template.query(HEADER_ACCESS_KEY, accessKeyValue);
    }

}
