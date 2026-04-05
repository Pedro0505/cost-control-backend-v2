package pedro.cost.control.domain.creditcard.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class InvoiceSummaryByYearMonth {
    private Integer invoiceMonth;
    private Integer invoiceYear;
    private BigDecimal total;
}
