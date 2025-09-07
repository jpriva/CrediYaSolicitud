package co.com.pragma.api.mapper.report;

import co.com.pragma.api.dto.reports.SolicitudeReportResponseDTO;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SolicitudeReportMapperImpl.class)
class SolicitudeReportMapperTest {

    @Autowired
    private SolicitudeReportMapper mapper;

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