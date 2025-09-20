package co.com.pragma.webclient.config;

import co.com.pragma.model.logs.gateways.LoggerPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = WebClientConfig.class)
class WebClientConfigDefaultPropertiesTest {

    @Autowired
    private WebClient webClient;

    @Autowired
    private WebClientProperties properties;

    @MockitoBean
    private LoggerPort logger;

    @Test
    void shouldCreateWebClientBeanWithDefaultValues() {
        assertThat(webClient).isNotNull();
        assertThat(properties.getUrl()).isEqualTo("http://localhost:8081");
        assertThat(properties.getTimeout()).isEqualTo(5000);
    }
}