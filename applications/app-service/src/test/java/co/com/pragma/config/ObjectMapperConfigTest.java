package co.com.pragma.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class ObjectMapperConfigTest {

    private ObjectMapperConfig objectMapperConfig;

    @BeforeEach
    void setUp() {
        objectMapperConfig = new ObjectMapperConfig();
    }

    @Test
    @DisplayName("Should create ObjectMapper with WRITE_DATES_AS_TIMESTAMPS disabled")
    void shouldCreateObjectMapperWithDatesAsTimestampsDisabled() {
        // Act
        ObjectMapper objectMapper = objectMapperConfig.objectMapper(new Jackson2ObjectMapperBuilder());

        // Assert
        assertNotNull(objectMapper);
        assertFalse(objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
    }

    @Test
    @DisplayName("Should serialize LocalDateTime as ISO-8601 string")
    void shouldSerializeLocalDateTimeAsISOString() throws JsonProcessingException {
        // Arrange
        ObjectMapper objectMapper = objectMapperConfig.objectMapper(new Jackson2ObjectMapperBuilder());
        LocalDateTime testDate = LocalDateTime.of(2024, Month.JULY, 26, 10, 30, 0);
        TestDto dto = new TestDto(testDate);
        String expectedJson = "{\"date\":\"2024-07-26T10:30:00\"}";

        // Act
        String actualJson = objectMapper.writeValueAsString(dto);

        // Assert
        assertEquals(expectedJson, actualJson);
    }

    @Getter
    @AllArgsConstructor
    private static class TestDto {
        private final LocalDateTime date;
    }
}