package pedro.cost.control.domain.creditcard.dtos;

import pedro.cost.control.domain.creditcard.emuns.DescriptionMappingType;
import pedro.cost.control.domain.creditcard.entities.DescriptionMapping;

public record DescriptionMappingOutputDTO(
        Long id,
        Integer position,
        String pattern,
        DescriptionMappingType type,
        String value
) {
    public static DescriptionMappingOutputDTO from(DescriptionMapping mapping) {
        return new DescriptionMappingOutputDTO(
                mapping.getId(),
                mapping.getPosition(),
                mapping.getPattern(),
                mapping.getType(),
                mapping.getValue()
        );
    }
}
