package co.com.pragma.sqs.sender.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class MoneySerializerTest {

    @Mock
    private JsonGenerator jsonGenerator;

    @Mock
    private SerializerProvider serializerProvider;

    private MoneySerializer moneySerializer;

    @BeforeEach
    void setUp() {
        moneySerializer = new MoneySerializer();
    }

    @Test
    @DisplayName("Should write null when value is null")
    void shouldWriteNullWhenValueIsNull() throws IOException {
        // Act
        moneySerializer.serialize(null, jsonGenerator, serializerProvider);

        // Assert
        verify(jsonGenerator).writeNull();
        verifyNoMoreInteractions(jsonGenerator);
    }

    @ParameterizedTest
    @CsvSource({
            "150.5,   150.50",
            "199.995, 200.00",
            "199.994, 199.99",
            "1000,    1000.00",
            "123.45,  123.45"
    })
    @DisplayName("Should serialize BigDecimal values correctly")
    void shouldSerializeBigDecimalValuesCorrectly(String inputValue, String expectedOutput) throws IOException {
        // Arrange
        BigDecimal value = new BigDecimal(inputValue);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        moneySerializer.serialize(value, jsonGenerator, serializerProvider);

        // Assert
        verify(jsonGenerator).writeString(stringCaptor.capture());
        assertEquals(expectedOutput, stringCaptor.getValue());
    }
}