package pedro.cost.control.domain.cost.mappers;

import org.mapstruct.Mapper;
import pedro.cost.control.domain.cost.dtos.CostSummaryOutputDTO;
import pedro.cost.control.domain.cost.entities.Cost;

@Mapper(componentModel = "spring")
public interface CostMapper {
    CostSummaryOutputDTO costToCostSummaryOutputDTO(Cost cost);
}
