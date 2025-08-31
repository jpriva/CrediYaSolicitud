package co.com.pragma.webclient.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = WebClientConfig.class)
@TestPropertySource(properties = {
        "adapters.restconsumer.url=https://example.com",
        "adapters.restconsumer.timeout=10000"
})
class WebClientConfigCustomPropertiesTest {

    @Autowired
    private WebClient webClient;

    @Autowired
    private WebClientProperties properties;

    @Test
    void shouldCreateWebClientBeanWithCustomValues() {
        assertThat(webClient).isNotNull();
        assertThat(properties.getUrl()).isEqualTo("https://example.com");
        assertThat(properties.getTimeout()).isEqualTo(10000);
    }
}