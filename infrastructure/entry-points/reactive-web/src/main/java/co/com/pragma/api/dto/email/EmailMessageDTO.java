package co.com.pragma.api.dto.email;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EmailMessageDTO {
    private String to;
    private String subject;
    private String body;
}
