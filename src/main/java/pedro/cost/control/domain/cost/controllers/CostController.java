package pedro.cost.control.domain.cost.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pedro.cost.control.domain.cost.dtos.CostOutputDTO;
import pedro.cost.control.domain.cost.dtos.CostSummaryOutputDTO;
import pedro.cost.control.domain.cost.dtos.CreateCostInputDTO;
import pedro.cost.control.domain.cost.dtos.ImportCostRecurrentInputDTO;
import pedro.cost.control.domain.cost.dtos.UpdateCostInputDTO;
import pedro.cost.control.domain.cost.services.CostService;

import java.util.List;

@RestController
@RequestMapping("/api/v2/cost")
@RequiredArgsConstructor
public class CostController {
    private final CostService costService;

    @GetMapping
    public ResponseEntity<List<CostOutputDTO>> getAllByYearAndMonth(@RequestParam(name = "year") Integer year,
                                                                    @RequestParam(name = "month") Integer month) {
        List<CostOutputDTO> costs = costService.getAllCostByYearMonth(year, month);

        return ResponseEntity.ok(costs);
    }

    @PostMapping
    public ResponseEntity<CostSummaryOutputDTO> create(@RequestBody CreateCostInputDTO createCostInputDTO) {
        CostSummaryOutputDTO costOutputDTO = costService.create(createCostInputDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(costOutputDTO);
    }

    @DeleteMapping
    public ResponseEntity<CostSummaryOutputDTO> delete(@RequestParam(name = "id") Long id) {
        CostSummaryOutputDTO deletedCost = costService.delete(id);

        return ResponseEntity.ok(deletedCost);
    }

    @PutMapping
    public ResponseEntity<CostSummaryOutputDTO> update(@RequestParam(name = "id") Long id, @RequestBody UpdateCostInputDTO updateCostInputDTO) {
        CostSummaryOutputDTO costUpdated = costService.update(id, updateCostInputDTO);

        return ResponseEntity.ok(costUpdated);
    }

    @PostMapping("/import-recurrent")
    public ResponseEntity<Void> importRecurrentCosts(@RequestBody ImportCostRecurrentInputDTO importCostRecurrentInputDTO) {
        costService.importRecurrentCosts(importCostRecurrentInputDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
