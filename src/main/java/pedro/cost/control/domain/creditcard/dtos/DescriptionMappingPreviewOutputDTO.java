package pedro.cost.control.domain.creditcard.dtos;


public record DescriptionMappingPreviewOutputDTO(
        String rawDescription,
        String normalizedDescription,
        boolean ignored,
        Long matchedMappingId,
        Integer matchedPosition
) {
}
