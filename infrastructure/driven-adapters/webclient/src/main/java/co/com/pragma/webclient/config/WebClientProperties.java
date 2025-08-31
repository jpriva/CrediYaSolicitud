package co.com.pragma.webclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "adapters.restconsumer")
public class WebClientProperties {

    private String url = "http://localhost:8081";
    private int timeout = 5000;

}