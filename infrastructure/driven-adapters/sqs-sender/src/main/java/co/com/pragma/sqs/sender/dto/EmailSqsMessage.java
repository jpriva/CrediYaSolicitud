package co.com.pragma.sqs.sender.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailSqsMessage {
    private String to;
    private String subject;
    private String body;
}