package pedro.cost.control.domain.creditcard.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pedro.cost.control.domain.creditcard.services.FileService;

@RestController
@RequestMapping("/api/v2/file")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping(value = "/invoices", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> processa(
            @RequestParam MultipartFile file,
            @RequestParam(name = "invoiceReferenceYear") Integer invoiceReferenceYear,
            @RequestParam(name = "invoiceReferenceMonth") Integer invoiceReferenceMonth
    ) {
        fileService.uploadInvoiceFile(file, invoiceReferenceYear, invoiceReferenceMonth);

        return ResponseEntity.noContent().build();
    }
}
