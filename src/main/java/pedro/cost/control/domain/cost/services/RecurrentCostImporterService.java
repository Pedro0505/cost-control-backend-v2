package pedro.cost.control.domain.cost.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.cost.dtos.ImportCostRecurrentInputDTO;
import pedro.cost.control.domain.cost.entities.Cost;
import pedro.cost.control.domain.cost.repositories.CostRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecurrentCostImporterService {

    private final CostRepository costRepository;

    public void importRecurrentCosts(ImportCostRecurrentInputDTO input, MonthlyBalance targetMonthlyBalance) {
        List<Cost> sourceCosts = costRepository.findAllRecurrentCostByYearMonth(input.getSourceReferenceYear(),input.getSourceReferenceMonth());

        List<Cost> newCosts = sourceCosts.stream().map(cost -> cloneCost(cost, targetMonthlyBalance)).toList();

        costRepository.saveAll(newCosts);
    }

    private Cost cloneCost(Cost source, MonthlyBalance targetBalance) {
        return Cost.builder()
                .amount(source.getAmount())
                .percentage(source.getPercentage())
                .calculationType(source.getCalculationType())
                .description(source.getDescription())
                .recurrent(source.getRecurrent())
                .monthlyBalance(targetBalance)
                .paid(source.getPaid())
                .build();
    }
}
