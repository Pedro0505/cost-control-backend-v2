package pedro.cost.control.domain.cost.mappers;

import org.mapstruct.Mapper;
import pedro.cost.control.domain.cost.dtos.CostSummaryOutputDTO;
import pedro.cost.control.domain.cost.dtos.PreviewRecurrentCostsForImportOutPutDTO;
import pedro.cost.control.domain.cost.entities.Cost;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CostMapper {
    CostSummaryOutputDTO costToCostSummaryOutputDTO(Cost cost);
    List<PreviewRecurrentCostsForImportOutPutDTO> costToPreviewRecurrentCostsForImportOutPutDTO(List<Cost> cost);
}
