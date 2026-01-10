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
import pedro.cost.control.domain.contract.dtos.EmploymentContractOutputDTO;
import pedro.cost.control.domain.contract.dtos.PjContractInputCreateDTO;
import pedro.cost.control.domain.contract.services.EmploymentContractService;

@RestController
@RequestMapping("/api/v2/contracts")
@RequiredArgsConstructor
public class ContractController {
    private final EmploymentContractService employmentContractService;

    @GetMapping
    public ResponseEntity<LegacyPageResponse<EmploymentContractOutputDTO>> getAllContractsPaged(
            @RequestParam Integer page, @RequestParam Integer size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("initDate", "endDate").descending());
        LegacyPageResponse<EmploymentContractOutputDTO> employmentContractPage = employmentContractService.getAllContractsPaged(pageable);

        return ResponseEntity.ok(employmentContractPage);
    }

    @PostMapping("/employment/pj")
    public ResponseEntity<Void> addNewPjContract(@RequestBody PjContractInputCreateDTO pjContractInputCreateDTO) {
        employmentContractService.addNewPjContract(pjContractInputCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
