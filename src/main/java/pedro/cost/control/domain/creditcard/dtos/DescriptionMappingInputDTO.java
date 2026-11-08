package pedro.cost.control.domain.creditcard.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pedro.cost.control.domain.creditcard.emuns.DescriptionMappingType;

/**
 * Corpo de criação e edição de um mapeamento.
 * <ul>
 *   <li>{@code type}: opcional, padrão CLASSIFY.</li>
 *   <li>{@code value}: obrigatório para CLASSIFY, ignorado para IGNORE.</li>
 *   <li>{@code position}: opcional (1-based). Na criação, sem posição vai para o fim;
 *       com posição, é inserido ali e quem estava daquela posição em diante desce uma casa.
 *       Na edição, sem posição mantém a posição atual; com posição, move o mapeamento.</li>
 * </ul>
 */
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
