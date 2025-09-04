package co.com.pragma.usecase.loantype;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class LoanTypeUseCase {
    private final LoanTypeRepository loanTypeRepository;

    public Flux<LoanType> getAllLoanTypes() {
        return loanTypeRepository.findAll();
    }
}
