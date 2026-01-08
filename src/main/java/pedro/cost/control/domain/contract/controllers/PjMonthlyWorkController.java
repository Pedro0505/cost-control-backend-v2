package pedro.cost.control.domain.contract.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pedro.cost.control.common.LegacyPageResponse;
import pedro.cost.control.domain.contract.dtos.PjMonthlyContractOutputDTO;
import pedro.cost.control.domain.contract.dtos.PjMonthlyWorkInputCreateDTO;
import pedro.cost.control.domain.contract.services.PjMonthlyWorkService;

import java.util.List;

@RestController
@RequestMapping("/api/v2/pj-monthly-work")
@RequiredArgsConstructor
public class PjMonthlyWorkController {
    private final PjMonthlyWorkService pjMonthlyWorkService;

    @PostMapping
    public ResponseEntity<Void> addNewPjContract(@RequestBody PjMonthlyWorkInputCreateDTO pjMonthlyWorkInputCreateDTO) {
        pjMonthlyWorkService.createPjMonthlyWork(pjMonthlyWorkInputCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<LegacyPageResponse<PjMonthlyContractOutputDTO>> getAllPjMonthlyWithContract(
            @RequestParam Integer page, @RequestParam Integer size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("referenceYear", "referenceMonth").descending());
        LegacyPageResponse<PjMonthlyContractOutputDTO> allPageable = pjMonthlyWorkService.getAllPjMonthlyWithContract(pageable);

        return ResponseEntity.ok(allPageable);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<PjMonthlyContractOutputDTO>> getAllPjMonthlyWithContractFiltered(
            @RequestParam Integer year
    ) {
        List<PjMonthlyContractOutputDTO> allPageable = pjMonthlyWorkService.getAllPjMonthlyWithContractFiltered(year);

        return ResponseEntity.ok(allPageable);
    }
}
