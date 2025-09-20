package co.com.pragma.api.dto.page;

import co.com.pragma.api.constants.ApiConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(
        name = ApiConstants.Schemas.PAGINATED_RESPONSE_SCHEMA_NAME,
        description = ApiConstants.Pageable.PAGINATED_RESPONSE_SCHEMA_DESC
)
public class PaginatedResponseDTO<T> {
    @Schema(description = ApiConstants.Pageable.PAGINATED_CONTENT_DESC)
    private List<T> content;
    @Schema(
            description = ApiConstants.Pageable.PAGINATED_CURRENT_PAGE_DESC,
            example = ApiConstants.Pageable.EXAMPLE_PAGINATED_CURRENT_PAGE
    )
    @JsonProperty("page")
    private int currentPage;
    @Schema(
            description = ApiConstants.Pageable.PAGINATED_PAGE_SIZE_DESC,
            example = ApiConstants.Pageable.EXAMPLE_PAGINATED_PAGE_SIZE
    )
    @JsonProperty("size")
    private int pageSize;
    @Schema(
            description = ApiConstants.Pageable.PAGINATED_TOTAL_ELEMENTS_DESC,
            example = ApiConstants.Pageable.EXAMPLE_PAGINATED_TOTAL_ELEMENTS
    )
    private long totalElements;
    @Schema(
            description = ApiConstants.Pageable.PAGINATED_TOTAL_PAGES_DESC,
            example = ApiConstants.Pageable.EXAMPLE_PAGINATED_TOTAL_PAGES
    )
    private int totalPages;
    @Schema(
            description = ApiConstants.Pageable.PAGINATED_HAS_NEXT_DESC,
            example = ApiConstants.Pageable.EXAMPLE_PAGINATED_HAS_NEXT
    )
    private boolean hasNext;
    @Schema(
            description = ApiConstants.Pageable.PAGINATED_HAS_PREVIOUS_DESC,
            example = ApiConstants.Pageable.EXAMPLE_PAGINATED_HAS_PREVIOUS
    )
    private boolean hasPrevious;
}