package pedro.cost.control.domain.creditcard.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pedro.cost.control.domain.creditcard.emuns.CreditCardCategoryType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreditCardCategoryCreateInputDTO {
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private CreditCardCategoryType type;
}
