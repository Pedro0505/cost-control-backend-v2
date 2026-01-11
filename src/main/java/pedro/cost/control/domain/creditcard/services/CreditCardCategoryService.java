package pedro.cost.control.domain.creditcard.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.config.exceptions.NotFoundException;
import pedro.cost.control.domain.creditcard.dtos.CreditCardCategoryCreateInputDTO;
import pedro.cost.control.domain.creditcard.emuns.CreditCardCategoryType;
import pedro.cost.control.domain.creditcard.entities.CreditCardCategory;
import pedro.cost.control.domain.creditcard.repositories.CreditCardCategoryRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditCardCategoryService {
    private final CreditCardCategoryRepository creditCardCategoryRepository;

    public CreditCardCategory getCardCategoryByType(CreditCardCategoryType cardCategoryType) {
        return creditCardCategoryRepository.findByCategoryType(cardCategoryType)
                .orElseThrow(() -> new NotFoundException("Categoria " + cardCategoryType.name() + " não encontrada"));
    }

    public Optional<CreditCardCategory> getById(Long id) {
        return creditCardCategoryRepository.findById(id);
    }

    public CreditCardCategory create(CreditCardCategoryCreateInputDTO categoryCreateInput) {
        CreditCardCategory creditCardCategoryToCreate = CreditCardCategory.builder()
                .type(categoryCreateInput.getType())
                .name(categoryCreateInput.getName())
                .build();

        return save(creditCardCategoryToCreate);
    }

    public CreditCardCategory save(CreditCardCategory category) {
        return creditCardCategoryRepository.save(category);
    }

    public CreditCardCategory getOrCreate(CreditCardCategoryCreateInputDTO category) {
        Optional<CreditCardCategory> optionalCreditCardCategory = getById(category.getId());

        return optionalCreditCardCategory.orElseGet(() -> create(category));

    }
}
