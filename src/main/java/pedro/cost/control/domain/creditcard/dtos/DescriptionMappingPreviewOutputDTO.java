package pedro.cost.control.domain.creditcard.dtos;

/**
 * Resultado de testar uma descrição contra as regras atuais.
 * {@code matchedMappingId}/{@code matchedPosition} são nulos quando nenhuma regra casa
 * (resultado "Outros"); {@code ignored} é true quando a regra que casou é IGNORE.
 */
public record DescriptionMappingPreviewOutputDTO(
        String rawDescription,
        String normalizedDescription,
        boolean ignored,
        Long matchedMappingId,
        Integer matchedPosition
) {
}
