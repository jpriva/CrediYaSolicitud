package co.com.pragma.webclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "adapters.restconsumer")
@Component
public class WebClientProperties {

    private String url = "http://localhost:8081";
    private int timeout = 5000;

}