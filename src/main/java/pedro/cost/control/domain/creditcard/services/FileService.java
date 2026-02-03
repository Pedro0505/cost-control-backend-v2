package pedro.cost.control.domain.creditcard.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pedro.cost.control.domain.creditcard.contexts.CostFileDiscriminationContext;
import pedro.cost.control.domain.creditcard.entities.CreditCardExpense;
import pedro.cost.control.domain.creditcard.implementations.ReadFilesImplementations;
import pedro.cost.control.domain.creditcard.mapper.CreditCardExpenseMapper;
import pedro.cost.control.utils.MonthYearUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

@Service
@RequiredArgsConstructor
public class FileService {
    private final Map<String, ReadFilesImplementations> readers;
    private final CreditCardExpensesHandler creditCardExpensesHandler;
    private final CreditCardExpenseMapper creditCardExpenseMapper;
    private final CreditCardExpenseService creditCardExpenseService;
    private final InvoicePeriodLockManager lockManager;

    @Transactional
    public void uploadInvoicesFiles(MultipartFile file, Integer invoiceReferenceYear, Integer invoiceReferenceMonth) {
        Lock lock = lockManager.acquire(invoiceReferenceYear, invoiceReferenceMonth);

        try {
            MonthYearUtils.validateMonthAndYear(invoiceReferenceYear, invoiceReferenceMonth);

            ReadFilesImplementations reader = readers.get(file.getContentType());

            List<CostFileDiscriminationContext> fileContent = reader.read(file);

            List<CostFileDiscriminationContext> normalized = creditCardExpensesHandler.getNormalizedExpensesDescriptions(
                    fileContent, invoiceReferenceYear, invoiceReferenceMonth
            );

            List<CreditCardExpense> expenses = creditCardExpenseMapper.toEntityList(normalized);

            creditCardExpenseService.deleteAllByYearAndMonth(invoiceReferenceYear, invoiceReferenceMonth);

            creditCardExpenseService.saveAll(expenses);
        } finally {
            lock.unlock();
        }
    }
}
