package pedro.cost.control.domain.cost.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ImportCostRecurrentInputDTO {
    private Integer sourceReferenceYear;
    private Integer sourceReferenceMonth;
    private Integer targetReferenceYear;
    private Integer targetReferenceMonth;
}
