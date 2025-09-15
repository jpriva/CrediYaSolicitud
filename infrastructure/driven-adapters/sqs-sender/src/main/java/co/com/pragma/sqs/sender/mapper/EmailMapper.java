package co.com.pragma.sqs.sender.mapper;

import co.com.pragma.model.template.EmailMessage;
import co.com.pragma.sqs.sender.dto.EmailSqsMessage;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface EmailMapper {
    EmailSqsMessage toSqsMessage(EmailMessage emailMessage);
}
