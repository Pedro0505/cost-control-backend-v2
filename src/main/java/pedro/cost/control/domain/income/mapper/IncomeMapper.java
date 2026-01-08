package pedro.cost.control.domain.income.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pedro.cost.control.domain.income.dtos.IncomeOutputDTO;
import pedro.cost.control.domain.income.entities.Income;

@Mapper(componentModel = "spring")
public interface IncomeMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "referenceDate", source = "referenceDate")
    @Mapping(target = "contractType", source = "employmentContract.contractType")
    IncomeOutputDTO toDto(Income income);
}
