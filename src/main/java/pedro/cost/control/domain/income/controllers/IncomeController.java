package pedro.cost.control.domain.income.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pedro.cost.control.common.LegacyPageResponse;
import pedro.cost.control.domain.income.dtos.IncomeInputCreateDTO;
import pedro.cost.control.domain.income.dtos.IncomeOutputDTO;
import pedro.cost.control.domain.income.services.IncomeService;

@RestController
@RequestMapping("/api/v2/incomes")
@RequiredArgsConstructor
public class IncomeController {
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<Void> createIncome(@RequestBody IncomeInputCreateDTO incomeInputCreateDTO) {
        incomeService.createIncome(incomeInputCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<LegacyPageResponse<IncomeOutputDTO>> getAllPageable(@RequestParam Integer page, @RequestParam Integer size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("referenceDate").descending());
        LegacyPageResponse<IncomeOutputDTO> allPageable = incomeService.getAllPageable(pageable);

        return ResponseEntity.ok(allPageable);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam Long id) {
        incomeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
