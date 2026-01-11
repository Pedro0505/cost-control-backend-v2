package pedro.cost.control.domain.cost.factories;

import org.springframework.stereotype.Component;
import pedro.cost.control.domain.cost.contexts.CostCreationContext;
import pedro.cost.control.domain.cost.dtos.CreateCostInputDTO;
import pedro.cost.control.domain.cost.entities.Cost;

@Component
public class CostFactory {

    public Cost create(CostCreationContext context) {
        CreateCostInputDTO input = context.getInput();

        return Cost.builder()
                .amount(context.getAmount())
                .percentage(input.getPercentage())
                .calculationType(input.getCalculationType())
                .description(input.getDescription())
                .recurrent(input.getRecurrent())
                .monthlyBalance(context.getMonthlyBalance())
                .paid(input.getPaid())
                .build();
    }
}