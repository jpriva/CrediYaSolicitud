package co.com.pragma.model.template;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class EmailMessage {
    private String to;
    private String subject;
    private String body;
}
