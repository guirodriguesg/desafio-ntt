package nttdata.bank.feign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignInterceptorConverteCambioConfig implements RequestInterceptor {

    private String ACCESS_KEY_VALUE = "d76c060a12d74fd42537ddf8f4411408";
    private static final String ACCESS_KEY = "access_key";

    @Override
    public void apply(RequestTemplate template) {
        template.query(ACCESS_KEY, ACCESS_KEY_VALUE);
    }

}
