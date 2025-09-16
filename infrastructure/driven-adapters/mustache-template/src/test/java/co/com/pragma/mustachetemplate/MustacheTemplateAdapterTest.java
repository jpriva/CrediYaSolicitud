package co.com.pragma.mustachetemplate;

import com.github.mustachejava.MustacheException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

class MustacheTemplateAdapterTest {

    private MustacheTemplateAdapter mustacheTemplateAdapter;

    @BeforeEach
    void setUp() {
        // Se instancia la clase directamente, ya que no tiene dependencias externas que necesiten ser mockeadas.
        mustacheTemplateAdapter = new MustacheTemplateAdapter();
    }

    @Test
    @DisplayName("Should process template correctly when given valid template name and context")
    void process_shouldRenderTemplateWithContext() {
        // Arrange
        String templateName = "test-template";
        Map<String, Object> context = Map.of(
                "name", "Juan",
                "amount", "$10,000",
                "status", "approved"
        );
        String expectedOutput = "Hello, Juan! Your loan for $10,000 has been approved.";

        // Act
        Mono<String> result = mustacheTemplateAdapter.process(templateName, context);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedOutput)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return a Mono error when template file does not exist")
    void process_shouldReturnMonoErrorForNonExistentTemplate() {
        // Arrange
        String templateName = "non-existent-template";
        Map<String, Object> context = Map.of();

        // Act
        Mono<String> result = mustacheTemplateAdapter.process(templateName, context);

        // Assert
        // StepVerifier se suscribe al Mono y verifica que termine con la señal de error esperada.
        StepVerifier.create(result)
                .expectError(MustacheException.class)
                .verify();
    }
}