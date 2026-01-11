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
import pedro.cost.control.domain.income.mapper.IncomeMapper;
import pedro.cost.control.domain.income.repositories.IncomeRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final IncomeCreationContextFactory incomeCreationContextFactory;
    private final IncomeFactory incomeFactory;
    private final PjIncomeHandler pjIncomeHandler;
    private final IncomeMapper incomeMapper;

    @Transactional
    public void createIncome(IncomeInputCreateDTO dto) {

        IncomeCreationContext context = incomeCreationContextFactory.create(dto);

        pjIncomeHandler.createPjMonthlyWorkToIncome(context.getInput(), context.getContractSummary());

        Income income = incomeFactory.create(context);

        save(income);
    }

    public Income findById(Long id) {
        Optional<Income> optionalIncome = incomeRepository.findById(id);

        if (optionalIncome.isEmpty()) {
            throw new NotFoundException("Renda não encontrada");
        }

        return optionalIncome.get();
    }

    public void delete(Long id) {
        Income incomeToDelete = findById(id);

        pjIncomeHandler.deletePjMonthlyWorkLinkedWithIncomeIfNecessary(incomeToDelete);

        incomeRepository.delete(incomeToDelete);
    }

    public void save(Income income) {
        incomeRepository.save(income);
    }

    public LegacyPageResponse<IncomeOutputDTO> getAllPageable(PageRequest pageable) {
        Page<IncomeOutputDTO> pageResult = incomeRepository.findAll(pageable).map(incomeMapper::toDto);

        return new LegacyPageResponse<>(pageResult);
    }

    public BigDecimal getTotalIncomeByYearAndMonth(Integer year, Integer month) {
        return incomeRepository.sumAmountByMonth(year, month)
                .orElseThrow(() -> new NotFoundException("Não foi encontradas entradas para o mês " + month + " e ano " + year));
    }
}
