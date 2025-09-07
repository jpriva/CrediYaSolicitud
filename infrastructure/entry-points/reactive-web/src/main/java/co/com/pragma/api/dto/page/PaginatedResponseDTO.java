package co.com.pragma.api.dto.page;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "PaginatedResponse", description = "Container for paginated list results.")
public class PaginatedResponseDTO<T> {
    @Schema(description = "The content of the current page.")
    private List<T> content;
    @Schema(description = "The current page number (0-indexed).", example = "0")
    @JsonProperty("page")
    private int currentPage;
    @Schema(description = "The number of elements requested per page.", example = "10")
    @JsonProperty("size")
    private int pageSize;
    @Schema(description = "The total number of elements across all pages.", example = "100")
    private long totalElements;
    @Schema(description = "The total number of pages available.", example = "10")
    private int totalPages;
    @Schema(description = "Indicates if there is a next page.", example = "true")
    private boolean hasNext;
    @Schema(description = "Indicates if there is a previous page.", example = "false")
    private boolean hasPrevious;
}