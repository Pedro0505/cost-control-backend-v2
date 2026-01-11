package pedro.cost.control.domain.creditcard.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pedro.cost.control.domain.creditcard.dtos.CreditCardCategoryRuleCreateInputDTO;
import pedro.cost.control.domain.creditcard.services.CreditCardCategoryRuleService;

@RestController
@RequestMapping("/api/v2/credit-card/category/rules")
@RequiredArgsConstructor
public class CreditCardCategoryRuleController {
    private final CreditCardCategoryRuleService cardCategoryRuleService;

    @PostMapping
    public ResponseEntity<Void> createCreditCardCategoryRule(
            @RequestBody CreditCardCategoryRuleCreateInputDTO creditCardCategoryRuleCreateInput) {
        cardCategoryRuleService.create(creditCardCategoryRuleCreateInput);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
