package pedro.cost.control.domain.creditcard.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pedro.cost.control.domain.creditcard.emuns.DescriptionMappingType;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DescriptionMappingInputDTO {
    private String pattern;
    private String value;
    private DescriptionMappingType type;
    private Integer position;
}
