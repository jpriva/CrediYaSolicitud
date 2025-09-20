package co.com.pragma.sqs.sender.mappers;

import co.com.pragma.model.template.EmailMessage;
import co.com.pragma.sqs.sender.dto.EmailSqsMessage;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class EmailMapperTest {

    private final co.com.pragma.sqs.sender.mapper.EmailMapper mapper = Mappers.getMapper(co.com.pragma.sqs.sender.mapper.EmailMapper.class);

    @Test
    void shouldMapEmailMessageToDto() {
        // Arrange
        EmailMessage domain = EmailMessage.builder()
                .to("test@example.com")
                .subject("Your Loan Status")
                .body("<html><body>Your loan has been approved.</body></html>")
                .build();

        // Act
        EmailSqsMessage dto = mapper.toSqsMessage(domain);

        // Assert
        assertNotNull(dto);
        assertEquals(domain.getTo(), dto.getTo());
        assertEquals(domain.getSubject(), dto.getSubject());
        assertEquals(domain.getBody(), dto.getBody());
    }

    @Test
    void shouldReturnNullWhenDomainIsNull() {
        assertNull(mapper.toSqsMessage(null));
    }
}
