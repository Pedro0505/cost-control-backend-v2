package pedro.cost.control.domain.contract.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pedro.cost.control.domain.contract.dtos.PjContractInputCreateDTO;
import pedro.cost.control.domain.contract.services.ContractService;

@RestController
@RequestMapping("/api/v2/contracts")
@RequiredArgsConstructor
public class ContractController {
    private final ContractService contractService;

    @PostMapping("/employment/pj")
    public ResponseEntity<Void> addNewPjContract(@RequestBody PjContractInputCreateDTO pjContractInputCreateDTO) {
        contractService.addNewPjContract(pjContractInputCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
