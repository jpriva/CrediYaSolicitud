package co.com.pragma.mustachetemplate;

import com.github.mustachejava.MustacheException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        String result = mustacheTemplateAdapter.process(templateName, context);

        // Assert
        assertEquals(expectedOutput, result);
    }

    @Test
    @DisplayName("Should throw MustacheException when template file does not exist")
    void process_shouldThrowExceptionForNonExistentTemplate() {
        // Arrange
        String templateName = "non-existent-template";
        Map<String, Object> context = Map.of();

        // Act & Assert
        // Se verifica que se lance la excepción esperada cuando la plantilla no se encuentra.
        assertThrows(MustacheException.class, () -> {
            mustacheTemplateAdapter.process(templateName, context);
        });
    }
}