package co.com.pragma.api.mapper.email;

import co.com.pragma.api.dto.email.EmailMessageDTO;
import co.com.pragma.model.template.EmailMessage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmailDTOMapper {
    EmailMessageDTO toDto(EmailMessage message);
}
