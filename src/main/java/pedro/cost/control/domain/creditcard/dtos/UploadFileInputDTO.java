package pedro.cost.control.domain.creditcard.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class UploadFileInputDTO {
    @NotNull
    @Min(1)
    @Max(12)
    private Integer invoiceReferenceMonth;

    @NotNull
    private Integer invoiceReferenceYear;
}
