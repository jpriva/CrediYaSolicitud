package co.com.pragma.api.mapper.page;

import co.com.pragma.api.dto.page.PaginatedResponseDTO;
import co.com.pragma.api.mapper.report.SolicitudeReportMapperImpl;
import co.com.pragma.model.page.PaginatedData;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PageMapperImpl.class, SolicitudeReportMapperImpl.class}) // Important: Test the MapStruct implementation
class PageMapperTest {

    @Autowired
    private PageMapper pageMapper;

    @Nested
    class ToDtoTests {

        @Test
        void shouldMapFullPaginatedDataToDto() {
            PaginatedData<SolicitudeReport> domainData = PaginatedData.<SolicitudeReport>builder()
                    .content(Collections.singletonList(new SolicitudeReport()))
                    .currentPage(1)
                    .pageSize(10)
                    .totalElements(25L)
                    .totalPages(3)
                    .hasNext(true)
                    .hasPrevious(true)
                    .build();

            PaginatedResponseDTO<?> dto = pageMapper.toDto(domainData);

            assertThat(dto).isNotNull();
            assertThat(dto.getCurrentPage()).isEqualTo(domainData.getCurrentPage());
            assertThat(dto.getPageSize()).isEqualTo(domainData.getPageSize());
            assertThat(dto.getTotalElements()).isEqualTo(domainData.getTotalElements());
            assertThat(dto.getTotalPages()).isEqualTo(domainData.getTotalPages());
            assertThat(dto.isHasNext()).isEqualTo(domainData.isHasNext());
            assertThat(dto.isHasPrevious()).isEqualTo(domainData.isHasPrevious());

            assertThat(dto.getContent()).isNotNull();
            assertThat(dto.getContent()).hasSize(1);
        }

        @Test
        void shouldReturnNullWhenDomainIsNull() {
            PaginatedResponseDTO<?> dto = pageMapper.toDto(null);

            assertThat(dto).isNull();
        }
    }
}