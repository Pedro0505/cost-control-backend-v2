package pedro.cost.control.domain.balance.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.balance.dtos.AvailableBalanceYearMonth;
import pedro.cost.control.domain.balance.dtos.MonthInfo;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.balance.repositories.MonthlyBalanceRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonthlyBalanceService {
    private final MonthlyBalanceRepository monthlyBalanceRepository;

    public MonthlyBalance save(MonthlyBalance monthlyBalance) {
        return monthlyBalanceRepository.save(monthlyBalance);
    }

    public Optional<MonthlyBalance> getMonthlyBalanceByYearAndMonth(Integer year, Integer month) {
        return monthlyBalanceRepository.findMonthlyBalanceByYearAndMonth(year, month);
    }

    public MonthlyBalance createMonthlyBalanceObject(Integer year, Integer month) {
        return MonthlyBalance.builder()
                .referenceYear(year)
                .referenceMonth(month)
                .build();
    }

    public MonthlyBalance getOrCreateMonthlyBalance(Integer year, Integer month) {
        Optional<MonthlyBalance> monthlyBalance = getMonthlyBalanceByYearAndMonth(year, month);
        MonthlyBalance monthlyBalanceToCreate = createMonthlyBalanceObject(year, month);

        return monthlyBalance.orElseGet(() -> save(monthlyBalanceToCreate));
    }


    public List<AvailableBalanceYearMonth> getAllMonthlyBalanceWithIncomeRelation() {
        List<MonthlyBalance> monthlyBalanceWithIncomeRelation = monthlyBalanceRepository.findAllMonthlyBalanceWithIncomeRelation();

        return monthlyBalanceWithIncomeRelation
                .stream()
                .collect(Collectors.groupingBy(MonthlyBalance::getReferenceYear))
                .entrySet()
                .stream()
                .map((e) -> new AvailableBalanceYearMonth(e.getKey(), e.getValue().stream().map(j -> new MonthInfo(j.getReferenceMonth())).toList()))
                .toList();
    }
}
