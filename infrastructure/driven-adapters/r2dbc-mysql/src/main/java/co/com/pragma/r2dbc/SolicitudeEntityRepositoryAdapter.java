package co.com.pragma.r2dbc;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import co.com.pragma.r2dbc.mapper.PersistenceSolicitudeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
@RequiredArgsConstructor
public class SolicitudeEntityRepositoryAdapter implements SolicitudeRepository {

    private final SolicitudeEntityRepository solicitudeRepository;
    private final PersistenceSolicitudeMapper solicitudeMapper;
    private final LoggerPort logger;

    @Override
    public Mono<Solicitude> save(Solicitude solicitude) {
        return solicitudeRepository.save(solicitudeMapper.toEntity(solicitude))
                .map(solicitudeMapper::toDomain);
    }

    @Override
    public Flux<SolicitudeReport> findSolicitudeReport(SolicitudeReportFilter filter) {
        return solicitudeRepository.findSolicitudeReport(filter);
    }

    @Override
    public Mono<BigDecimal> findTotalMonthlyFee(String email) {
        return solicitudeRepository.findTotalMonthlyFee(email);
    }

    @Override
    public Mono<Long> countSolicitudeReport(SolicitudeReportFilter filter) {
        return solicitudeRepository.countSolicitudeReport(filter);
    }

    @Override
    public Mono<Solicitude> findById(Integer solicitudeId) {
        return solicitudeRepository.findById(solicitudeId).map(solicitudeMapper::toDomain);
    }

    @Override
    public Flux<Solicitude> findByEmail(String email) {
        return solicitudeRepository.findByEmail(email).map(solicitudeMapper::toDomain);
    }
}
