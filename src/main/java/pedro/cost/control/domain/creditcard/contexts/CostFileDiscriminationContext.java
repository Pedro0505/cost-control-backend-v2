package pedro.cost.control.domain.creditcard.contexts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CostFileDiscriminationContext {
    private LocalDate date;
    private String description;
    private Double amount;
}
