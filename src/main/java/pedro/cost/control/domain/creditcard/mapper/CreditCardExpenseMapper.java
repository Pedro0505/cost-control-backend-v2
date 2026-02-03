package pedro.cost.control.domain.creditcard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pedro.cost.control.domain.creditcard.contexts.CostFileDiscriminationContext;
import pedro.cost.control.domain.creditcard.entities.CreditCardExpense;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CreditCardExpenseMapper {
    @Mapping(target = "rawDescription", source = "rawDescription")
    @Mapping(target = "normalizedDescription", source = "description")
    @Mapping(target = "expenseDate", source = "date")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "invoiceReferenceYear", source = "invoiceReferenceYear")
    @Mapping(target = "invoiceReferenceMonth", source = "invoiceReferenceMonth")
    CreditCardExpense toEntity(CostFileDiscriminationContext source);

    List<CreditCardExpense> toEntityList(List<CostFileDiscriminationContext> source);
}
