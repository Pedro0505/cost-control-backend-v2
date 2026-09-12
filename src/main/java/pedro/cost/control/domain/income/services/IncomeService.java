package pedro.cost.control.domain.income.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pedro.cost.control.common.LegacyPageResponse;
import pedro.cost.control.config.exceptions.NotFoundException;
import pedro.cost.control.domain.income.contexts.IncomeCreationContext;
import pedro.cost.control.domain.income.dtos.IncomeInputCreateDTO;
import pedro.cost.control.domain.income.dtos.IncomeOutputDTO;
import pedro.cost.control.domain.income.entities.Income;
import pedro.cost.control.domain.income.factories.IncomeCreationContextFactory;
import pedro.cost.control.domain.income.factories.IncomeFactory;
import pedro.cost.control.domain.income.repositories.IncomeRepository;
import pedro.cost.control.security.CustomUserDetails;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final IncomeCreationContextFactory incomeCreationContextFactory;
    private final IncomeFactory incomeFactory;
    private final PjIncomeHandler pjIncomeHandler;

    @Transactional
    public void createIncome(IncomeInputCreateDTO dto, CustomUserDetails userDetails) {

        IncomeCreationContext context = incomeCreationContextFactory.create(dto, userDetails.getUser());

        pjIncomeHandler.createPjMonthlyWorkToIncome(context.getInput(), context.getContractSummary(), userDetails.getId());

        Income income = incomeFactory.create(context, userDetails.getUser());

        save(income);
    }

    public Income findById(Long id, Long userId) {
        Optional<Income> optionalIncome = incomeRepository.findIncomeByIdAndUserId(id, userId);

        if (optionalIncome.isEmpty()) {
            throw new NotFoundException("Renda não encontrada");
        }

        return optionalIncome.get();
    }

    public void delete(Long id, Long userId) {
        Income incomeToDelete = findById(id, userId);

        pjIncomeHandler.deletePjMonthlyWorkLinkedWithIncomeIfNecessary(incomeToDelete, userId);

        incomeRepository.delete(incomeToDelete);
    }

    public void save(Income income) {
        incomeRepository.save(income);
    }

    public LegacyPageResponse<IncomeOutputDTO> getAllPageable(PageRequest pageable, Long userId) {
        Page<IncomeOutputDTO> pageResult = incomeRepository.findAllByUserId(userId, pageable);

        return new LegacyPageResponse<>(pageResult);
    }

    public BigDecimal getTotalIncomeByYearAndMonth(Integer year, Integer month, Long userId) {
        return incomeRepository.sumAmountByMonth(year, month, userId)
                .orElseThrow(() -> new NotFoundException("Não foi encontradas entradas para o mês " + month + " e ano " + year));
    }
}
