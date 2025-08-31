package co.com.pragma.api.dto;

import co.com.pragma.api.constants.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = Constants.ERROR_SCHEMA_NAME, description = Constants.ERROR_SCHEMA_DESCRIPTION)
public class ErrorDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Schema(description = Constants.ERROR_TIMESTAMP_DESCRIPTION, example = Constants.EXAMPLE_ERROR_TIMESTAMP)
    private Instant timestamp;

    @Schema(description = Constants.ERROR_PATH_DESCRIPTION, example = Constants.API_SOLICITUDE_PATH)
    private String path;

    @Schema(description = Constants.ERROR_CODE_DESCRIPTION, example = Constants.EXAMPLE_ERROR_CODE)
    private String code;

    @Schema(description = Constants.ERROR_MESSAGE_DESCRIPTION, example = Constants.EXAMPLE_ERROR_MESSAGE)
    private String message;
}
