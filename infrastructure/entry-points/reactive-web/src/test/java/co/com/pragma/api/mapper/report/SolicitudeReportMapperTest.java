package co.com.pragma.api.mapper.report;

import co.com.pragma.api.dto.reports.SolicitudeReportRequestDTO;
import co.com.pragma.api.dto.reports.SolicitudeReportResponseDTO;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SolicitudeReportMapperImpl.class) // Load the MapStruct implementation
class SolicitudeReportMapperTest {

    @Autowired
    private SolicitudeReportMapper mapper;

    @Nested
    class ToDomainTests {

        @Test
        void shouldMapFullRequestDtoToDomainFilter() {
            SolicitudeReportRequestDTO dto = SolicitudeReportRequestDTO.builder()
                    .clientEmail("test@example.com")
                    .clientName("John")
                    .clientIdNumber("12345")
                    .loanTypeName("PERSONAL")
                    .state("APROBADO")
                    .minValue(new BigDecimal("1000"))
                    .maxValue(new BigDecimal("5000"))
                    .minBaseSalary(new BigDecimal("2000"))
                    .maxBaseSalary(new BigDecimal("8000"))
                    .page(1)
                    .size(20)
                    .sortBy("value")
                    .sortDirection("DESC")
                    .build();

            SolicitudeReportFilter filter = mapper.toDomain(dto);

            assertThat(filter).isNotNull();
            assertThat(filter.getClientEmail()).isEqualTo(dto.getClientEmail());
            assertThat(filter.getClientName()).isEqualTo(dto.getClientName());
            assertThat(filter.getClientIdNumber()).isEqualTo(dto.getClientIdNumber());
            assertThat(filter.getLoanTypeName()).isEqualTo(dto.getLoanTypeName());
            assertThat(filter.getStateName()).isEqualTo(dto.getState()); // Verify the explicit mapping
            assertThat(filter.getMinValue()).isEqualTo(dto.getMinValue());
            assertThat(filter.getMaxValue()).isEqualTo(dto.getMaxValue());
            assertThat(filter.getMinBaseSalary()).isEqualTo(dto.getMinBaseSalary());
            assertThat(filter.getMaxBaseSalary()).isEqualTo(dto.getMaxBaseSalary());
            assertThat(filter.getPage()).isEqualTo(dto.getPage());
            assertThat(filter.getSize()).isEqualTo(dto.getSize());
            assertThat(filter.getSortBy()).isEqualTo(dto.getSortBy());
            assertThat(filter.getSortDirection()).isEqualTo(dto.getSortDirection());
        }

        @Test
        void shouldMapPartialRequestDtoToDomainFilter() {
            SolicitudeReportRequestDTO dto = SolicitudeReportRequestDTO.builder()
                    .state("PENDIENTE")
                    .minValue(new BigDecimal("500"))
                    .build();

            SolicitudeReportFilter filter = mapper.toDomain(dto);

            assertThat(filter).isNotNull();
            assertThat(filter.getStateName()).isEqualTo("PENDIENTE");
            assertThat(filter.getMinValue()).isEqualTo(new BigDecimal("500"));
            assertThat(filter.getClientEmail()).isNull();
            assertThat(filter.getMaxValue()).isNull();
        }

        @Test
        void shouldReturnNullWhenRequestDtoIsNull() {
            assertThat(mapper.toDomain(null)).isNull();
        }
    }

    @Nested
    class ToResponseDtoTests {

        @Test
        void shouldMapFullDomainReportToResponseDto() {
            SolicitudeReport report = SolicitudeReport.builder()
                    .solicitudeId(101)
                    .value(new BigDecimal("15000.00"))
                    .deadline(24)
                    .clientEmail("jane.doe@example.com")
                    .clientName("Jane Doe")
                    .clientIdentification("98765")
                    .loanTypeName("VEHICULO")
                    .interestRate(new BigDecimal("9.5"))
                    .stateName("APROBADO")
                    .baseSalary(new BigDecimal("60000"))
                    .totalMonthlyPay(new BigDecimal("750.55"))
                    .build();

            SolicitudeReportResponseDTO dto = mapper.toResponseDto(report);

            assertThat(dto).isNotNull();
            assertThat(dto.getSolicitudeId()).isEqualTo(report.getSolicitudeId());
            assertThat(dto.getValue()).isEqualTo(report.getValue());
            assertThat(dto.getDeadline()).isEqualTo(report.getDeadline());
            assertThat(dto.getClientEmail()).isEqualTo(report.getClientEmail());
            assertThat(dto.getClientName()).isEqualTo(report.getClientName());
            assertThat(dto.getClientIdentification()).isEqualTo(report.getClientIdentification());
            assertThat(dto.getLoanTypeName()).isEqualTo(report.getLoanTypeName());
            assertThat(dto.getInterestRate()).isEqualTo(report.getInterestRate());
            assertThat(dto.getStateName()).isEqualTo(report.getStateName());
            assertThat(dto.getBaseSalary()).isEqualTo(report.getBaseSalary());
            assertThat(dto.getTotalMonthlyPay()).isEqualTo(report.getTotalMonthlyPay());
        }

        @Test
        void shouldMapPartialDomainReportToResponseDto() {
            SolicitudeReport report = SolicitudeReport.builder()
                    .solicitudeId(102)
                    .clientEmail("partial@example.com")
                    .build();

            SolicitudeReportResponseDTO dto = mapper.toResponseDto(report);

            assertThat(dto).isNotNull();
            assertThat(dto.getSolicitudeId()).isEqualTo(102);
            assertThat(dto.getClientEmail()).isEqualTo("partial@example.com");
            assertThat(dto.getValue()).isNull();
            assertThat(dto.getClientName()).isNull();
        }

        @Test
        void shouldReturnNullWhenDomainReportIsNull() {
            assertThat(mapper.toResponseDto(null)).isNull();
        }
    }
}