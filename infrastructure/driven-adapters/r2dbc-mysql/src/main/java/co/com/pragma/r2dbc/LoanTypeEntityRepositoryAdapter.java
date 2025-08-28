package co.com.pragma.r2dbc;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.r2dbc.mapper.PersistenceLoanTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class LoanTypeEntityRepositoryAdapter implements LoanTypeRepository {
    private final LoanTypeEntityRepository loanTypeRepository;
    private final PersistenceLoanTypeMapper loanTypeMapper;

    @Override
    public Flux<LoanType> findAll() {
        return null;
    }

    @Override
    public Mono<LoanType> findOne(LoanType example) {
        return loanTypeRepository.findOne(Example.of(loanTypeMapper.toEntity(example)))
                .map(loanTypeMapper::toDomain);
    }
}