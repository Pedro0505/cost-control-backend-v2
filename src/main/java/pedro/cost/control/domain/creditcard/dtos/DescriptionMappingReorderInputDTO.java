package pedro.cost.control.domain.creditcard.dtos;

import java.util.List;

/**
 * Nova ordem completa: todos os ids de mapeamento do usuário, cada um uma única vez,
 * do mais prioritário para o menos prioritário.
 */
public record DescriptionMappingReorderInputDTO(List<Long> ids) {
}
