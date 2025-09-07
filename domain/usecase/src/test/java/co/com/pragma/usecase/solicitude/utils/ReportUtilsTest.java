package co.com.pragma.usecase.solicitude.utils;

import co.com.pragma.model.page.PageableData;
import co.com.pragma.model.page.PaginatedData;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import co.com.pragma.model.user.UserProjection;
import co.com.pragma.model.user.gateways.UserPort;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportUtilsTest {

    @Mock
    private UserPort userPort;

    @Nested
    class BuildReportTests {

        @Test
        void shouldEnrichReportWhenUserIsNotNull() {
            SolicitudeReport report = SolicitudeReport.builder().clientEmail("test@test.com").build();
            UserProjection user = UserProjection.builder()
                    .name("Test User")
                    .idNumber("123")
                    .baseSalary(new BigDecimal("50000"))
                    .build();

            SolicitudeReport result = ReportUtils.buildReport(report, user);

            assertThat(result.getClientName()).isEqualTo("Test User");
            assertThat(result.getClientIdentification()).isEqualTo("123");
            assertThat(result.getBaseSalary()).isEqualTo(new BigDecimal("50000"));
        }

        @Test
        void shouldNotEnrichReportWhenUserIsNull() {
            SolicitudeReport report = SolicitudeReport.builder()
                    .clientEmail("test@test.com")
                    .clientName("Original Name") // Pre-existing value
                    .build();

            SolicitudeReport result = ReportUtils.buildReport(report, null);

            assertThat(result.getClientName()).isNull();
            assertThat(result.getClientIdentification()).isNull();
            assertThat(result.getBaseSalary()).isNull();
        }
    }

    @Nested
    class GetUserDataTests {

        @Test
        void shouldFetchAndCombineUserDataForDistinctEmails() {
            SolicitudeReport report1 = SolicitudeReport.builder().clientEmail("user1@test.com").build();
            SolicitudeReport report2 = SolicitudeReport.builder().clientEmail("user2@test.com").build();
            SolicitudeReport report3 = SolicitudeReport.builder().clientEmail("user1@test.com").build(); // Duplicate email

            UserProjection user1 = UserProjection.builder().email("user1@test.com").name("User One").build();
            UserProjection user2 = UserProjection.builder().email("user2@test.com").name("User Two").build();

            when(userPort.getUsersByEmails(anyList())).thenReturn(Flux.just(user1, user2));

            Flux<SolicitudeReport> result = ReportUtils.getUserData(userPort, List.of(report1, report2, report3));

            StepVerifier.create(result)
                    .expectNextMatches(r -> "User One".equals(r.getClientName()))
                    .expectNextMatches(r -> "User Two".equals(r.getClientName()))
                    .expectNextMatches(r -> "User One".equals(r.getClientName()))
                    .verifyComplete();

            ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
            verify(userPort).getUsersByEmails(captor.capture());
            assertThat(captor.getValue()).containsExactlyInAnyOrder("user1@test.com", "user2@test.com");
        }

        @Test
        void shouldHandleEmptyUserResponse() {
            SolicitudeReport report1 = SolicitudeReport.builder().clientEmail("user1@test.com").build();
            when(userPort.getUsersByEmails(anyList())).thenReturn(Flux.empty());

            Flux<SolicitudeReport> result = ReportUtils.getUserData(userPort, List.of(report1));

            StepVerifier.create(result)
                    .expectNextMatches(r -> r.getClientName() == null)
                    .verifyComplete();
        }

        @Test
        void shouldHandleEmptySolicitudeList() {
            Flux<SolicitudeReport> result = ReportUtils.getUserData(userPort, Collections.emptyList());

            StepVerifier.create(result).verifyComplete();
            verify(userPort, never()).getUsersByEmails(anyList());
        }
    }

    @Nested
    class HasClientFiltersTests {

        static Stream<Arguments> filterProvider() {
            return Stream.of(
                    Arguments.of(SolicitudeReportFilter.builder().clientEmail("test").build(), true),
                    Arguments.of(SolicitudeReportFilter.builder().clientEmail("").build(), false),
                    Arguments.of(SolicitudeReportFilter.builder().clientName("test").build(), true),
                    Arguments.of(SolicitudeReportFilter.builder().clientName("").build(), false),
                    Arguments.of(SolicitudeReportFilter.builder().clientIdNumber("123").build(), true),
                    Arguments.of(SolicitudeReportFilter.builder().clientIdNumber("").build(), false),
                    Arguments.of(SolicitudeReportFilter.builder().minBaseSalary(BigDecimal.ONE).build(), true),
                    Arguments.of(SolicitudeReportFilter.builder().maxBaseSalary(BigDecimal.TEN).build(), true),
                    Arguments.of(SolicitudeReportFilter.builder().stateName("PENDIENTE").build(), false),
                    Arguments.of(SolicitudeReportFilter.builder().loanTypeName("PERSONAL").build(), false),
                    Arguments.of(SolicitudeReportFilter.builder().build(), false),
                    Arguments.of(SolicitudeReportFilter.builder().clientName(" ").build(), false) // Blank should be false
            );
        }

        @ParameterizedTest
        @MethodSource("filterProvider")
        void shouldCorrectlyDetectClientFilters(SolicitudeReportFilter filter, boolean expectedResult) {
            boolean result = ReportUtils.hasClientFilters(filter);

            assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class buildPaginated{
        @Test
        void buildPaginated_shouldCreatePaginatedDataSolicitudeReportWithData_whenListHasSize(){
            SolicitudeReport report = SolicitudeReport.builder()
                    .solicitudeId(1)
                    .build();
            List<SolicitudeReport> page = List.of(report);
            PageableData pageable = PageableData.builder()
                    .page(0)
                    .size(10)
                    .build();
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder()
                    .pageable(pageable)
                    .build();
            Long totalElements = 1L;

            PaginatedData<SolicitudeReport> paged = ReportUtils.buildPaginated(filter,page,totalElements);

            assertThat(paged).isNotNull();
            assertThat(paged.getContent()).isNotNull();
            assertThat(paged.getContent()).hasSize(1);
            assertThat(paged.getCurrentPage()).isZero();
            assertThat(paged.getPageSize()).isEqualTo(10);
            assertThat(paged.getTotalElements()).isEqualTo(totalElements);
            assertThat(paged.getTotalPages()).isZero();
            assertThat(paged.isHasNext()).isFalse();
            assertThat(paged.isHasPrevious()).isFalse();

        }

        @Test
        void buildPaginated_shouldCreatePaginatedDataSolicitudeReportWithData_whenFilterHasThreePages(){
            SolicitudeReport report = SolicitudeReport.builder()
                    .solicitudeId(1)
                    .build();
            List<SolicitudeReport> page = List.of(report,report,report);
            PageableData pageable = PageableData.builder()
                    .page(1)
                    .size(1)
                    .build();
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder()
                    .pageable(pageable)
                    .build();
            Long totalElements = 3L;

            PaginatedData<SolicitudeReport> paged = ReportUtils.buildPaginated(filter,page,totalElements);

            assertThat(paged).isNotNull();
            assertThat(paged.getContent()).isNotNull();
            assertThat(paged.getContent()).hasSize(3);
            assertThat(paged.getCurrentPage()).isEqualTo(1);
            assertThat(paged.getPageSize()).isEqualTo(1);
            assertThat(paged.getTotalElements()).isEqualTo(totalElements);
            assertThat(paged.getTotalPages()).isEqualTo(3);
            assertThat(paged.isHasNext()).isTrue();
            assertThat(paged.isHasPrevious()).isTrue();

        }
    }
}