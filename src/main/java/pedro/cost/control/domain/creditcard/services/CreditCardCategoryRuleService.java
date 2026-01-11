package pedro.cost.control.domain.creditcard.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.creditcard.dtos.CreditCardCategoryRuleCreateInputDTO;
import pedro.cost.control.domain.creditcard.entities.CreditCardCategory;
import pedro.cost.control.domain.creditcard.entities.CreditCardCategoryRule;
import pedro.cost.control.domain.creditcard.repositories.CreditCardCategoryRuleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditCardCategoryRuleService {
    private final CreditCardCategoryRuleRepository creditCardCategoryRuleRepository;
    private final CreditCardCategoryService creditCardCategoryService;

    public List<CreditCardCategoryRule> getByActiveTrue() {
        return creditCardCategoryRuleRepository.findByActiveTrue();
    }

    @Transactional
    public void create(CreditCardCategoryRuleCreateInputDTO ruleCreateInput) {
        CreditCardCategory category = creditCardCategoryService.getOrCreate(ruleCreateInput.getCategory());

        CreditCardCategoryRule rule = new CreditCardCategoryRule();
        rule.setCategory(category);
        rule.setPattern(ruleCreateInput.getPattern());

        creditCardCategoryRuleRepository.save(rule);
    }
}
