package co.com.pragma.model.page;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PageableData {
    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;
}
