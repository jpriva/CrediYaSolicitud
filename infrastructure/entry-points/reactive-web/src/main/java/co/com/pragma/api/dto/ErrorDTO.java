package co.com.pragma.api.dto;

import co.com.pragma.api.constants.ApiConstants;
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
@Schema(name = ApiConstants.Schemas.ERROR_SCHEMA_NAME, description = ApiConstants.Schemas.ERROR_SCHEMA_DESCRIPTION)
public class ErrorDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Schema(description = ApiConstants.ErrorDocs.ERROR_TIMESTAMP_DESCRIPTION, example = ApiConstants.ErrorDocs.EXAMPLE_ERROR_TIMESTAMP)
    private Instant timestamp;

    @Schema(description = ApiConstants.ErrorDocs.ERROR_PATH_DESCRIPTION, example = ApiConstants.ApiPath.SOLICITUDE_PATH)
    private String path;

    @Schema(description = ApiConstants.ErrorDocs.ERROR_CODE_DESCRIPTION, example = ApiConstants.ErrorDocs.EXAMPLE_ERROR_CODE)
    private String code;

    @Schema(description = ApiConstants.ErrorDocs.ERROR_MESSAGE_DESCRIPTION, example = ApiConstants.ErrorDocs.EXAMPLE_ERROR_MESSAGE)
    private String message;
}
