package co.com.pragma.r2dbc;

import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.r2dbc.mapper.PersistenceSolicitudeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class SolicitudeEntityRepositoryAdapter implements SolicitudeRepository {

    private final SolicitudeEntityRepository solicitudeRepository;
    private final PersistenceSolicitudeMapper solicitudeMapper;

    @Override
    public Mono<Solicitude> save(Solicitude solicitude) {
        return solicitudeRepository.save(solicitudeMapper.toEntity(solicitude))
                .map(solicitudeMapper::toDomain);
    }
}
