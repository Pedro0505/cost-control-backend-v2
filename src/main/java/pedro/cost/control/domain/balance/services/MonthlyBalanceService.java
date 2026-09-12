package pedro.cost.control.domain.balance.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.balance.dtos.AvailableBalanceYearMonth;
import pedro.cost.control.domain.balance.dtos.MonthInfo;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.balance.repositories.MonthlyBalanceRepository;
import pedro.cost.control.domain.user.entities.User;

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

    public Optional<MonthlyBalance> getMonthlyBalanceByYearAndMonth(Integer year, Integer month, Long userId) {
        return monthlyBalanceRepository.findMonthlyBalanceByYearAndMonth(year, month, userId);
    }

    public MonthlyBalance createMonthlyBalanceObject(Integer year, Integer month, User user) {
        return MonthlyBalance.builder()
                .referenceYear(year)
                .referenceMonth(month)
                .user(user)
                .build();
    }

    public MonthlyBalance getOrCreateMonthlyBalance(Integer year, Integer month, User user) {
        Optional<MonthlyBalance> monthlyBalance = getMonthlyBalanceByYearAndMonth(year, month, user.getId());
        MonthlyBalance monthlyBalanceToCreate = createMonthlyBalanceObject(year, month, user);

        return monthlyBalance.orElseGet(() -> save(monthlyBalanceToCreate));
    }


    public List<AvailableBalanceYearMonth> getAllMonthlyBalanceWithIncomeRelation(Long userId) {
        List<MonthlyBalance> monthlyBalanceWithIncomeRelation = monthlyBalanceRepository.findAllMonthlyBalanceWithIncomeRelation(userId);

        return monthlyBalanceWithIncomeRelation
                .stream()
                .collect(Collectors.groupingBy(MonthlyBalance::getReferenceYear))
                .entrySet()
                .stream()
                .map(e -> new AvailableBalanceYearMonth(e.getKey(), e.getValue().stream().map(j -> new MonthInfo(j.getReferenceMonth())).toList()))
                .toList();
    }
}
