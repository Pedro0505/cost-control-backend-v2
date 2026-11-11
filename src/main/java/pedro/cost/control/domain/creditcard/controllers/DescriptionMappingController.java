package pedro.cost.control.domain.creditcard.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pedro.cost.control.domain.creditcard.dtos.DescriptionMappingInputDTO;
import pedro.cost.control.domain.creditcard.dtos.DescriptionMappingOutputDTO;
import pedro.cost.control.domain.creditcard.dtos.DescriptionMappingPreviewOutputDTO;
import pedro.cost.control.domain.creditcard.dtos.DescriptionMappingReorderInputDTO;
import pedro.cost.control.domain.creditcard.services.DescriptionMappingService;
import pedro.cost.control.security.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/v2/description-mappings")
@RequiredArgsConstructor
public class DescriptionMappingController {
    private final DescriptionMappingService descriptionMappingService;

    @GetMapping
    public ResponseEntity<List<DescriptionMappingOutputDTO>> findAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(descriptionMappingService.findAll(userDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DescriptionMappingOutputDTO> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(descriptionMappingService.findById(id, userDetails.getId()));
    }

    @PostMapping
    public ResponseEntity<DescriptionMappingOutputDTO> create(
            @RequestBody DescriptionMappingInputDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(descriptionMappingService.create(dto, userDetails.getUser()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DescriptionMappingOutputDTO> update(
            @PathVariable Long id,
            @RequestBody DescriptionMappingInputDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(descriptionMappingService.update(id, dto, userDetails.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        descriptionMappingService.delete(id, userDetails.getId());

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/order")
    public ResponseEntity<List<DescriptionMappingOutputDTO>> reorder(
            @RequestBody DescriptionMappingReorderInputDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(descriptionMappingService.reorder(dto, userDetails.getId()));
    }

    @PostMapping("/defaults")
    public ResponseEntity<List<DescriptionMappingOutputDTO>> loadDefaults(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(descriptionMappingService.loadDefaults(userDetails.getUser()));
    }

    @GetMapping("/preview")
    public ResponseEntity<DescriptionMappingPreviewOutputDTO> preview(
            @RequestParam String description,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(descriptionMappingService.preview(description, userDetails.getId()));
    }
}
